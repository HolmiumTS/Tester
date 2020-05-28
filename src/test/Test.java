package test;

public class Test {
    /**
     * init a test
     *
     * @param stdInput       data of the std input
     * @param stdOutput      data of the std output
     * @param compileCommand exactly compile command
     * @param runCommand     exactly run command
     * @param timeLimitMs    max running time in ms
     */
    public Test(String[] stdInput, String[] stdOutput, String compileCommand, String runCommand, long timeLimitMs) {

    }

    /**
     * run the test
     *
     * @return the test result
     * @throws TestException if sth wrong
     * @see TestResult
     */
    public TestResult run() throws TestException {
        return TestResult.AC;
    }
}
