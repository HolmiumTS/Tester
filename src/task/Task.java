package task;

import data.DataException;
import data.TaskData;
import data.TestPoint;
import test.Test;
import test.TestResult;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;

public class Task {
    String root;
    String compileCommand;
    String runCommand;
    String runCheckerCommand;
    TaskType type;
    int timeLimitMs;

    /**
     * init a task
     *
     * @param root root dir of the Task
     */
    public Task(String root) {
        this.root = root;
    }

    private boolean init() {
        try (Scanner scanner = new Scanner(new File("." + File.pathSeparator + "config.txt"))) {
            String s = scanner.nextLine();
            switch (s.trim()) {
                case "FULL_COMPARE":
                    type = TaskType.FULL_COMPARE;
                    break;
                case "SPECIAL_JUDGE":
                    type = TaskType.SPECIAL_JUDGE;
                    break;
                default:
                    return false;
            }
            if (!scanner.hasNext()) {
                return false;
            }
            compileCommand = scanner.nextLine().trim();
            if (!scanner.hasNext()) {
                return false;
            }
            runCommand = scanner.nextLine().trim();
            if (!scanner.hasNextInt()) {
                return false;
            }
            timeLimitMs = scanner.nextInt();
            if (type == TaskType.SPECIAL_JUDGE) {
                if (!scanner.hasNext()) {
                    return false;
                }
                runCheckerCommand = scanner.nextLine().trim();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private List<? extends Callable<?>> packTest(String[] papers, TestPoint[] points) {
        List<PackTest> tests = new ArrayList<>();
        for (final String paper : papers) {
            for (final TestPoint point : points) {
                tests.add(new PackTest(
                        paper,
                        point.getName(),
                        new Test(
                                point.getInput(),
                                point.getOutput(),
                                compileCommand.replaceAll("%s", paper),
                                runCommand.replaceAll("%s", paper),
                                timeLimitMs
                        )
                ));
            }
        }
        return tests;
    }

    /**
     * run a task
     *
     * @throws TaskException if something wrong
     */
    public void run() throws TaskException {
        if (!init()) {
            throw new TaskException("Error in config file.");
        }
        File code = new File(root + File.pathSeparator + "code");
        if (!code.exists() || !code.isDirectory()) {
            throw new TaskException("Cannot find code.");
        }
        TestPoint[] points;
        try {
            points = TaskData.getTestPoint(root + File.pathSeparator + "data");
        } catch (DataException e) {
            throw new TaskException("Error in test data loading.", e);
        }
        String[] papers = getAllPapers(code);
        if (papers == null) {
            throw new TaskException("Error in test papers loading.");
        }
        List<? extends Callable<?>> list;
        if (type == TaskType.FULL_COMPARE) {
            list = packTest(papers, points);
        }
    }

    TaskResult execute(List<? extends Callable<?>> list) {
        ExecutorCompletionService service = new ExecutorCompletionService<>(Executors.newCachedThreadPool());
        for (Callable callable : list) {
            service.submit(callable);
        }
        TaskResult result = new TaskResult();
        return result;
    }

    String[] getAllPapers(File code) {
        File[] files = code.listFiles();
        if (files == null) return null;
        List<String> list = new ArrayList<>();
        for (File f : files) {
            if (!f.isDirectory()) {
                continue;
            }
            list.add(f.getName());
        }
        return (String[]) list.toArray();
    }

    private class TaskResult {
        private final Map<String, Map<String, TestResult>> res = new HashMap<>();

        public void collect(TestUnit unit) {
            if (res.get(unit.getPaper()) == null) {
                res.put(unit.getPaper(), new HashMap<>());
            }
            res.get(unit.getPaper()).put(unit.getTestPoint(), unit.getResult());
        }
    }
}
