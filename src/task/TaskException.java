package task;

import data.DataException;

/**
 * task exception
 *
 * @author holmium
 */
public class TaskException extends Exception {
    public TaskException(String s) {
        super(s);
    }

    public TaskException(String s, Throwable e) {
        super(s, e);
    }
}
