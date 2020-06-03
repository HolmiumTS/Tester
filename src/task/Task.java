package task;

import data.DataException;
import data.TaskData;
import data.TestPoint;
import test.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

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
        List<PackTest> tests = new ArrayList<>();
        for (final String paper : papers) {
            tests.addAll(
                    Arrays.stream(points)
                            .map(testPoint ->
                                    new PackTest(
                                            paper,
                                            testPoint.getName(),
                                            new Test(
                                                    testPoint.getInput(),
                                                    testPoint.getOutput(),
                                                    compileCommand.replaceAll("%s", paper),
                                                    runCommand.replaceAll("%s", paper),
                                                    timeLimitMs))
                            )
                            .collect(Collectors.toList())
            );
        }
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
}
