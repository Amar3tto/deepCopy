import java.util.Arrays;

public class TestInner {

    private boolean bo;
    private double d;
    private float f;
    private byte by;
    private int i;
    private long l;
    private char c;

    private Integer[][] intMatrix;

    //TODO:
//    private TestEntity testEntity;

    public TestInner(boolean bo, double d, float f, byte by, int i, long l, char c, Integer[][] intMatrix) {
        this.bo = bo;
        this.d = d;
        this.f = f;
        this.by = by;
        this.i = i;
        this.l = l;
        this.c = c;
        this.intMatrix = intMatrix;
    }

//    public void setTestEntity(TestEntity testEntity) {
//        this.testEntity = testEntity;
//    }


    @Override
    public String toString() {
        return "TestInner{" +
                "bo=" + bo +
                ", d=" + d +
                ", f=" + f +
                ", by=" + by +
                ", i=" + i +
                ", l=" + l +
                ", c=" + c +
                ", intMatrix=" + toString(intMatrix) +
                '}';
    }

    private String toString(Integer[][] intMatrix) {
        StringBuilder s = new StringBuilder();
        s.append(" ");
        for (Integer[] intArray : intMatrix) {
            for(Integer value: intArray) {
                s.append(value).append(" ");
            }
        }
        return s.toString();
    }
}
