package task;

import data.DataException;
import data.TaskData;
import data.TestPoint;
import test.Communicate;
import test.SpecialJudge;
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

/**
 * core of the tester
 * a task means 'test all paper with all test point'
 * this class will be create by Cli
 * types of task is defined in TaskType
 *
 * @author holmium
 * @see Test
 * @see TaskType
 * @see cli.Cli
 */
public class Task {
    String root;
    String compileCommand;
    String runCommand;
    String runCheckerCommand;
    TaskType type;
    int timeLimitMs;

    /**
     * init a task with a root path
     *
     * @param root root dir of the Task
     */
    public Task(String root) {
        this.root = root;
    }

    /**
     * load the config.txt under the root path
     * init the vars that test need
     *
     * @return true if load successfully, otherwise return false
     * @see TaskType
     */
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
                case "COMMUNICATE":
                    type = TaskType.COMMUNICATE;
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
            if (type != TaskType.FULL_COMPARE) {
                if (!scanner.hasNext()) {
                    return false;
                }
                scanner.nextLine();
                runCheckerCommand = scanner.nextLine().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * package all papers with all point into PackTest
     * only used to package for FULL_COMPARE task
     *
     * @param papers string array contains all papers' name
     * @param points string array contains all test points' name
     * @return a list, contains all packaged test
     * @see TaskType
     */
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
     * package all papers with all point into PackTest
     * only used to package for SPECIAL_JUDGE task
     *
     * @param papers string array contains all papers' name
     * @param points string array contains all test points' name
     * @return a list, contains all packaged test
     * @see TaskType
     */
    private List<? extends Callable<?>> packSpj(String[] papers, TestPoint[] points) {
        List<PackTest> tests = new ArrayList<>();
        for (final String paper : papers) {
            for (final TestPoint point : points) {
                tests.add(new PackTest(
                        paper,
                        point.getName(),
                        new SpecialJudge(
                                point.getInput(),
                                point.getOutput(),
                                compileCommand.replaceAll("%s", paper),
                                runCommand.replaceAll("%s", paper),
                                Paths.get(root, "code", paper).toString(),
                                timeLimitMs,
                                runCheckerCommand
                        )
                ));
                System.out.println(compileCommand.replaceAll("%s", paper));
            }
        }
        return tests;
    }

    /**
     * package all papers with all point into PackTest
     * only used to package for COMMUNICATE task
     *
     * @param papers string array contains all papers' name
     * @param points string array contains all test points' name
     * @return a list, contains all packaged test
     * @see TaskType
     */
    private List<? extends Callable<?>> packComm(String[] papers, TestPoint[] points) {
        List<PackTest> tests = new ArrayList<>();
        for (final String paper : papers) {
            for (final TestPoint point : points) {
                tests.add(new PackTest(
                        paper,
                        point.getName(),
                        new Communicate(
                                point.getInput(),
                                point.getOutput(),
                                compileCommand.replaceAll("%s", paper),
                                runCommand.replaceAll("%s", paper),
                                Paths.get(root, "code", paper).toString(),
                                timeLimitMs,
                                runCheckerCommand
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
        // init
        if (!init()) {
            throw new TaskException("Error in config file.");
        }
        // load code
        File code = new File(root + File.separator + "code");
        if (!code.exists() || !code.isDirectory()) {
            throw new TaskException("Cannot find code.");
        }
        // load data
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
        // pack paper and test point with different task type
        List<? extends Callable<?>> list = null;
        if (type == TaskType.FULL_COMPARE) {
            list = packTest(papers, points);
        } else if (type == TaskType.SPECIAL_JUDGE) {
            list = packSpj(papers, points);
        } else if (type == TaskType.COMMUNICATE) {
            list = packComm(papers, points);
        }
        // get and write result
        TaskResult res = execute(list);
        try {
            res.writeResult();
        } catch (IOException e) {
            throw new TaskException("Failed to write task result.", e);
        }
    }

    /**
     * run all test and collect their result
     * use ExecutorService to test concurrently
     * use ExecutorCompletionService to collect completed tests
     *
     * @param list list including PackTest
     * @return a TaskResult, contains all result from tests
     * @see TaskResult
     * @see ExecutorService
     * @see ExecutorCompletionService
     */
    TaskResult execute(List<? extends Callable<?>> list) {
        ExecutorService s = Executors.newCachedThreadPool();
        ExecutorCompletionService service = new ExecutorCompletionService<>(s);
        // submit test to ExecutorService
        for (Callable callable : list) {
            service.submit(callable);
        }
        TaskResult result = new TaskResult();
        int n = list.size();
        // Collect result from ExecutorCompletionService
        for (int i = 0; i < n; i++) {
            try {
                result.collect((TestUnit) service.take().get());
            } catch (InterruptedException e) {
//                e.printStackTrace();
            } catch (ExecutionException e) {
//                e.printStackTrace();
            }
        }
        // close ExecutorService
        s.shutdown();
        return result;
    }

    /**
     * get all papers from the code folder under root path
     *
     * @param code represent the code folder
     * @return all papers' name
     */
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

    /**
     * inner class to collect result
     * used to print the result report
     *
     * @author holmium
     * @see Task
     */
    private class TaskResult {
        private final Map<String, Map<String, TestResult>> res = new HashMap<>();

        /**
         * collect one test's result
         *
         * @param unit test result unit from execute PackTest
         * @see TestUnit
         * @see PackTest
         */
        public void collect(TestUnit unit) {
//            System.out.println(unit.paper + " " + unit.testPoint + " " + unit.result);
            if (res.get(unit.getPaper()) == null) {
                res.put(unit.getPaper(), new HashMap<>());
            }
            res.get(unit.getPaper()).put(unit.getTestPoint(), unit.getResult());
        }

        /**
         * write the result report to result.csv
         * write in the 'comma separated values' format
         *
         * @throws IOException
         */
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
            // write cols' name
            builder.append("\"name\",");
            for (String s : cols) {
                builder.append("\"").append(s).append("\",");
            }
            builder.append(System.lineSeparator());
            out.write(builder.toString());
            for (String row : rows) {
                // write each paper's result
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
