package task;

import test.TestResult;

import java.util.HashMap;
import java.util.Map;

public class TaskResult {
    private final Map<String, Map<String, TestResult>> res = new HashMap<>();

    public void collect(TestUnit unit) {
        if (res.get(unit.getPaper()) == null) {
            res.put(unit.getPaper(), new HashMap<>());
        }
        res.get(unit.getPaper()).put(unit.getTestPoint(), unit.getResult());
    }
}
