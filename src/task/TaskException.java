package task;

import data.DataException;

public class TaskException extends Exception {
    public TaskException(String s) {
        super(s);
    }

    public TaskException(String s, Throwable e) {
        super(s, e);
    }
}
