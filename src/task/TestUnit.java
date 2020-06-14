package task;

import test.TestResult;

/**
 * package the test result
 *
 * @author holmium
 * @see test.Test
 * @see PackTest
 */
public class TestUnit {
    String paper;
    String testPoint;
    TestResult result;

    TestUnit(String paper, String testPoint, TestResult result) {
        this.paper = paper;
        this.testPoint = testPoint;
        this.result = result;
    }

    public String getPaper() {
        return paper;
    }

    public String getTestPoint() {
        return testPoint;
    }

    public TestResult getResult() {
        return result;
    }
}
