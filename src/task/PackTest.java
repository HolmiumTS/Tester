package task;

import test.Test;

import java.util.concurrent.Callable;

class PackTest implements Callable<TestUnit> {
    String paper;
    String testPoint;
    Test test;

    PackTest(String paper, String testPoint, Test test) {
        this.paper = paper;
        this.testPoint = testPoint;
        this.test = test;
    }

    @Override
    public TestUnit call() throws Exception {
        System.out.println(paper + " " + testPoint);
        return new TestUnit(paper, testPoint, test.run());
    }
}
