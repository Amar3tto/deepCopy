import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class CopyUtils {

    //TODO: recursive

    /**
     * @param object        need to copy this
     * @param copiedObjects map of already copied objects, key is pair of class and hashcode, value is copied object
     * @return copy of {@param object}
     */
    public static <T> T deepCopy(T object, Map<Pair<Class, Integer>, Object> copiedObjects) throws IllegalAccessException {

        Class<?> tClass = object.getClass();
        if (tClass.isPrimitive()) {
            return object;
        }
        if (copiedObjects == null) {
            copiedObjects = new HashMap<>();
        }
        T copy;
        Pair<Class, Integer> objectKey = new ImmutablePair<>(tClass, object.hashCode());
        if (copiedObjects.containsKey(objectKey)) {
            return (T) copiedObjects.get(objectKey);
        }
        if (tClass.isPrimitive()) {
            return object;
        }
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
                        arrayCopy[i] = deepCopy(nextArrayObj, copiedObjects);
                        copiedObjects.put(arrayObjectKey, arrayCopy[i]);
                    }
                }
            }
            return (T) arrayCopy;
        } else {
            copy = (T) getEmptyObjectOf(tClass);
        }

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

                if (fieldValue != null && !fieldType.isPrimitive()) {

                    Pair<Class, Integer> fieldObjectKey = new ImmutablePair<>(fieldValue.getClass(), fieldValue.hashCode());
                    if (copiedObjects.containsKey(fieldObjectKey)) {
                        fieldValue = copiedObjects.get(fieldObjectKey);
                    } else {
                        fieldValue = deepCopy(fieldValue, copiedObjects);
                        copiedObjects.put(fieldObjectKey, fieldValue);
                    }
                }
                field.set(copy, fieldValue);
            }
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
                //Suppress
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
        String entityStr = entity.toString();
        System.out.println(entityStr);
        try {
            TestEntity copy = deepCopy(entity, copiedObjects);
            String copyStr = copy.toString();
            System.out.println(copyStr);
            System.out.println("Equal: " + entityStr.equals(copyStr));
//            System.out.println(copiedObjects.values());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
