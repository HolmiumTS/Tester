package data;

public class DataException extends Exception {
  String error;
  DataException(String error){
    this.error=error;
  }
}
