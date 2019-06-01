public class TestInner {

    protected boolean bo;
    protected double d;
    protected float f;
    protected byte by;
    protected int i;
    protected long l;
    protected char c;

    protected Integer[][] intMatrix;

    protected TestEntity testEntity;

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

    public void setTestEntity(TestEntity testEntity) {
        this.testEntity = testEntity;
    }


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
                ", testEntity=" + testEntity.hashCode() +
                ", intMatrix=" + toString(intMatrix) +
                '}';
    }

    /**
     * Custom print of matrix {@param intMatrix}
     */
    protected String toString(Integer[][] intMatrix) {
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
