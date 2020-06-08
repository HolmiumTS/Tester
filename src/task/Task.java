package task;

import data.DataException;
import data.TaskData;
import data.TestPoint;
import test.Test;
import test.TestResult;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

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
        try (Scanner scanner = new Scanner(new File(root + File.separator + "config.txt"))) {
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
            e.printStackTrace();
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
                                Paths.get(root, "code", paper).toString(),
                                timeLimitMs
                        )
                ));
                System.out.println(compileCommand.replaceAll("%s", paper));
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
        File code = new File(root + File.separator + "code");
        if (!code.exists() || !code.isDirectory()) {
            throw new TaskException("Cannot find code.");
        }
        TestPoint[] points;
        try {
            points = TaskData.getTestPoint(root + File.separator + "data");
        } catch (DataException e) {
            throw new TaskException("Error in test data loading.", e);
        }
        String[] papers = getAllPapers(code);
        if (papers == null) {
            throw new TaskException("Error in test papers loading.");
        }
        List<? extends Callable<?>> list = null;
        if (type == TaskType.FULL_COMPARE) {
            list = packTest(papers, points);
        }
        TaskResult res = execute(list);
        try {
            res.writeResult();
        } catch (IOException e) {
            throw new TaskException("Failed to write task result.", e);
        }
    }

    TaskResult execute(List<? extends Callable<?>> list) {
        ExecutorService s = Executors.newCachedThreadPool();
        ExecutorCompletionService service = new ExecutorCompletionService<>(s);
        for (Callable callable : list) {
            service.submit(callable);
        }
        TaskResult result = new TaskResult();
        int n = list.size();
        for (int i = 0; i < n; i++) {
            try {
                result.collect((TestUnit) service.take().get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        s.shutdown();
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
        return list.toArray(new String[0]);
    }

    private class TaskResult {
        private final Map<String, Map<String, TestResult>> res = new HashMap<>();

        public void collect(TestUnit unit) {
            if (res.get(unit.getPaper()) == null) {
                res.put(unit.getPaper(), new HashMap<>());
            }
            res.get(unit.getPaper()).put(unit.getTestPoint(), unit.getResult());
        }

        public void writeResult() throws IOException {
            Path resPath = new File(root + File.separator + "result.csv").toPath();
            if (Files.notExists(resPath)) {
                Files.createFile(resPath);
            }
            Writer out = Files.newBufferedWriter(resPath);
            if (res.size() == 0) {
                return;
            }
            String[] rows = res.keySet().toArray(new String[0]);
            String[] cols = res.get(rows[0]).keySet().toArray(new String[0]);
            if (cols.length == 0) return;
            StringBuilder builder = new StringBuilder();
            builder.append("\"name\",");
            for (String s : cols) {
                builder.append("\"").append(s).append("\",");
            }
            builder.append(System.lineSeparator());
            out.write(builder.toString());
            for (String row : rows) {
                StringBuilder sb = new StringBuilder();
                sb.append("\"").append(row).append("\",");
                for (String col : cols) {
                    sb.append("\"").append(((TestResult) res.get(row).get(col)).toString()).append("\",");
                }
                sb.append(System.lineSeparator());
                out.write(sb.toString());
            }
            out.close();
        }
    }
}
