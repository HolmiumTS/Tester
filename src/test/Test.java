package test;

import java.io.IOException;
import java.io.InputStream;

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

  public static void main(String[] args) {
    try {
      Process test = Runtime.getRuntime().exec("echo test >> test.txt");
    } catch (IOException e) {
      System.out.println("error occur");
      System.out.println(e.getCause());
    }
  }
}
