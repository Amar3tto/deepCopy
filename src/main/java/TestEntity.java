import java.util.Arrays;
import java.util.List;

public class TestEntity {

    private boolean bo;
    private double d;
    private float f;
    private byte by;
    private int i;
    private long l;
    private char c;

    private String s;

    private Boolean[] bArray;

    private List<Integer> integerList;

    private TestInner testInner;

    //TODO:
    private TestEntity testEntity;

    public TestEntity() {
    }

    public TestEntity(String s, boolean bo, double d, float f, byte by, int i, long l, char c, TestInner testInner, List<Integer> integerList) {
        this.s = s;
        this.bo = bo;
        this.d = d;
        this.f = f;
        this.by = by;
        this.i = i;
        this.l = l;
        this.c = c;
        this.testInner = testInner;
        bArray = new Boolean[] {bo};
        this.integerList = integerList;
//        this.testEntity = this;
    }

    @Override
    public String toString() {
        return "TestEntity{" +
                "bo=" + bo +
                ", s=" + s +
                ", d=" + d +
                ", f=" + f +
                ", by=" + by +
                ", i=" + i +
                ", l=" + l +
                ", c=" + c +
                ", integerList=" + (integerList != null ? integerList : "null") +
                ", testInner=" + (testInner != null ? testInner : "null") +
                ", testInner=" + (bArray != null ? Arrays.toString(bArray) : "null") +
                '}';
    }

    public List<Integer> getIntegerList() {
        return integerList;
    }
}
