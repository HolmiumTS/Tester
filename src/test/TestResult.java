package test;

/**
 * represent the test result
 *
 * @author holmium
 * @see Test
 */
public enum TestResult {
    /**
     * correctly, no problem
     */
    AC,
    /**
     * compile failed
     */
    CE,
    /**
     * paper finished abnormally
     */
    RE,
    /**
     * paper cannot finish in time
     */
    TLE,
    /**
     * paper get a wrong answer
     */
    WA
}
