package test;

/**
 * pack the test result
 * not use now
 *
 * @author AI
 * @see TestResult
 */
public class Result {

    private TestResult result;

    Result() {
        result = TestResult.RE;
    }

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }
}
