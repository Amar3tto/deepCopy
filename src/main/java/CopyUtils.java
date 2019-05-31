import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class CopyUtils {

    /**
     * @param object        need to copy this
     * @param copiedObjects map of already copied objects, key is pair of class and hashcode, value is copied object
     * @param needToCopy    map of all objects that need to copy, value is the list of {@link Callback} that will be called,
     *                      when copy of corresponding object will be ready
     * @return copy of {@param object}
     */
    public static <T> T deepCopy(T object, Map<Pair<Class, Integer>, Object> copiedObjects,
                                 Map<Pair<Class, Integer>, List<Callback>> needToCopy) throws IllegalAccessException {

        Class<?> tClass = object.getClass();
        if (tClass.isPrimitive()) {
            return object;
        }
        if (copiedObjects == null) {
            copiedObjects = new HashMap<>();
        }
        if (needToCopy == null) {
            needToCopy = new HashMap<>();
        }
        Pair<Class, Integer> objectKey = new ImmutablePair<>(tClass, object.hashCode());
        if (copiedObjects.containsKey(objectKey)) {
            return (T) copiedObjects.get(objectKey);
        }
        needToCopy.put(objectKey, new ArrayList<>());
        T copy;


        //Get copy of any array
        if (tClass.isArray()) {
            Class<?> componentType = tClass.getComponentType();
            if (componentType.isPrimitive()) {
                return object;
            }
            int arrayLength = Array.getLength(object);

            Object[] arrayCopy = (Object[]) Array.newInstance(componentType, arrayLength);
            for (int i = 0; i < arrayLength; i++) {
                Object nextArrayObj = ((Object[]) object)[i];
                if (nextArrayObj != null) {
                    Pair<Class, Integer> arrayObjectKey = new ImmutablePair<>(nextArrayObj.getClass(), nextArrayObj.hashCode());
                    if (copiedObjects.containsKey(arrayObjectKey)) {
                        arrayCopy[i] = copiedObjects.get(arrayObjectKey);
                    } else {
                        if (needToCopy.containsKey(arrayObjectKey)) {
                            //Add callback
                            final int arrayCopyIndex = i;
                            needToCopy.get(arrayObjectKey).add(readyCopy -> arrayCopy[arrayCopyIndex] = readyCopy);
                        } else {
                            needToCopy.put(arrayObjectKey, new ArrayList<>());
                            arrayCopy[i] = deepCopy(nextArrayObj, copiedObjects, needToCopy);
                            copiedObjects.put(arrayObjectKey, arrayCopy[i]);

                            //Pass ready array object copy to all callbacks
                            for (Callback callback : needToCopy.get(arrayObjectKey)) {
                                callback.callBack(arrayCopy[i]);
                            }
                        }
                    }
                }
            }
            return (T) arrayCopy;
        } else {
            copy = (T) getEmptyObjectOf(tClass);
        }


        //Get copy of all fields
        List<Field> allFields = new ArrayList<>();
        while (!tClass.equals(Object.class)) {
            allFields.addAll(Arrays.stream(tClass.getDeclaredFields()).collect(Collectors.toList()));
            tClass = tClass.getSuperclass();
        }
        for (Field field : allFields) {
            field.setAccessible(true);

            if (!Modifier.isStatic(field.getModifiers())) {

                Object fieldValue = field.get(object);
                Class<?> fieldType = field.getType();
                Object fieldValueCopy;

                if (fieldValue != null && !fieldType.isPrimitive()) {

                    Pair<Class, Integer> fieldObjectKey = new ImmutablePair<>(fieldValue.getClass(), fieldValue.hashCode());
                    if (copiedObjects.containsKey(fieldObjectKey)) {
                        fieldValueCopy = copiedObjects.get(fieldObjectKey);
                        field.set(copy, fieldValueCopy);
                    } else {
                        if (needToCopy.containsKey(fieldObjectKey)) {
                            //Add callback
                            needToCopy.get(fieldObjectKey).add(readyCopy -> {
                                try {
                                    field.set(copy, readyCopy);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            needToCopy.put(fieldObjectKey, new ArrayList<>());
                            fieldValueCopy = deepCopy(fieldValue, copiedObjects, needToCopy);
                            copiedObjects.put(fieldObjectKey, fieldValueCopy);
                            field.set(copy, fieldValueCopy);

                            //Pass ready field copy to all callbacks
                            for (Callback callback : needToCopy.get(fieldObjectKey)) {
                                callback.callBack(fieldValueCopy);
                            }
                        }
                    }
                } else {
                    field.set(copy, fieldValue);
                }
            }
        }

        //Pass ready object copy to all callbacks
        for (Callback callback : needToCopy.get(objectKey)) {
            callback.callBack(copy);
        }
        return copy;
    }

    /**
     * @return empty {@link Object} of {@param tClass},
     * Be aware that it will return zero-length array if {@param tClass} is array
     */
    private static Object getEmptyObjectOf(Class tClass) {
        Object copy;
        while (!tClass.equals(Object.class)) {
            try {
                copy = tClass.newInstance();
                return copy;
            } catch (IllegalAccessException | InstantiationException e) {
                //Suppress exceptions, it was just try to initialize instance with no args constructor
            }

            if (tClass.isArray()) {
                return Array.newInstance(tClass.getComponentType(), 0);
            }

            for (Constructor constructor : tClass.getConstructors()) {

                constructor.setAccessible(true);
                Class[] parameterTypes = constructor.getParameterTypes();
                if (parameterTypes.length == 0) {
                    continue;
                }
                Object[] parameters = Arrays.stream(parameterTypes).map(type ->
                        type.isPrimitive() ? getEmptyPrimitive(type) : getEmptyObjectOf(type)).toArray();
                try {
                    copy = constructor.newInstance(parameters);
                    return copy;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            tClass = tClass.getSuperclass();
        }
        return new Object();
    }

    /**
     * @return empty primitive {@link Object} of {@param tClass}
     */
    private static Object getEmptyPrimitive(Class tClass) {
        if (tClass.equals(Boolean.TYPE)) {
            return false;
        }
        if (tClass.equals(Character.TYPE)) {
            return 'c';
        }
        if (tClass.equals(Byte.TYPE)) {
            return Byte.parseByte("00000000");
        }
        if (tClass.equals(Short.TYPE)) {
            return 0;
        }
        if (tClass.equals(Double.TYPE)) {
            return 0d;
        }
        if (tClass.equals(Integer.TYPE)) {
            return 0;
        }
        if (tClass.equals(Float.TYPE)) {
            return 0f;
        }
        if (tClass.equals(Long.TYPE)) {
            return 0L;
        }
        return null;
    }

    public static void main(String[] args) {

        Map<Pair<Class, Integer>, Object> copiedObjects = new HashMap<>();
        byte b = 0b10;
        List<Integer> integerList = new ArrayList<>();
        integerList.add(5);

        TestInner inner = new TestInner(true, 0.35d, 0.2f, b, 4, 6, 'i', new Integer[][]{{1, 2}, {3, 4}});
        TestEntity entity = new TestEntity("My string", true, 0.25d, 0.1f, b, 2, 3, 'c', inner, integerList);
        inner.setTestEntity(entity);

        String entityStr = entity.toString();
        System.out.println(entityStr);
        try {
            TestEntity copy = deepCopy(entity, copiedObjects, null);
            String copyStr = copy.toString();
            System.out.println(copyStr);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
