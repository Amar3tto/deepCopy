import java.util.List;

public class TestChild extends TestInner {

    private List<TestInner> innerList;

    public TestChild(boolean bo, double d, float f, byte by, int i, long l, char c, Integer[][] intMatrix) {
        super(bo, d, f, by, i, l, c, intMatrix);
    }

    @Override
    public String toString() {
        return "TestChild{" +
                "innerList=" + innerList +
                ", bo=" + bo +
                ", d=" + d +
                ", f=" + f +
                ", by=" + by +
                ", i=" + i +
                ", l=" + l +
                ", c=" + c +
                ", intMatrix=" + toString(intMatrix) +
                ", testEntity=" + testEntity.hashCode() +
                '}';
    }

    public void setInnerList(List<TestInner> innerList) {
        this.innerList = innerList;
    }
}
