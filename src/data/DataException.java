package data;

/**
 * Exception for data loading
 * @see TaskData
 * @author AI
 */
public class DataException extends Exception {
    String error;

    DataException(String error) {
        this.error = error;
    }
}
