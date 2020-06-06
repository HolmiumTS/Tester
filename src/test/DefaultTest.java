package test;

public class DefaultTest implements TestWay {
  @Override
  public  boolean matchWay(String[] stdInput, String[] input) {
    if (stdInput.length != input.length) {
      return false;
    } else {
      for (int i = 0; i < stdInput.length; i++) {
        if (!stdInput[i].equals(input[i])) {
          return false;
        }
      }
    }
    return true;
  }
}
