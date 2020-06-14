package task;

import test.Test;

import java.util.concurrent.Callable;

/**
 * package the test
 * it is a callable s that it can be submit to a thread pool
 *
 * @author holmium
 * @see Test
 * @see Task
 * @see TestUnit
 * @see Callable
 */
class PackTest implements Callable<TestUnit> {
    String paper;
    String testPoint;
    Test test;

    /**
     * create a new PackTest
     *
     * @param paper     paper's name, which means it is who code
     * @param testPoint test point's name, means which test point
     * @param test      test need to be execute and get result
     */
    PackTest(String paper, String testPoint, Test test) {
        this.paper = paper;
        this.testPoint = testPoint;
        this.test = test;
    }

    /**
     * execute Test from interface Callable
     *
     * @return TestUnit, which contains running result, paper name and test point name
     * @throws Exception
     * @see Callable
     * @see TestUnit
     */
    @Override
    public TestUnit call() throws Exception {
//        System.out.println(paper + " " + testPoint);
        return new TestUnit(paper, testPoint, test.run());
    }
}
