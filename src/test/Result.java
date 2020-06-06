package test;

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
