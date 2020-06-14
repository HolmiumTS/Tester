package test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * test in full text compare mode
 * ignore space at line end
 *
 * @author holmium
 * @see SpecialJudge
 * @see Communicate
 */
public class Test {
    /**
     * std input
     */
    protected String[] input;
    /**
     * std output
     */
    protected String[] output;
    /**
     * answer not use
     */
    protected String[] answer;
    /**
     * command to compile the paper
     */
    protected String compileCmd;
    /**
     * command to execute the paper
     */
    protected String runCmd;
    /**
     * paper's root path
     */
    protected String path;
    /**
     * max time in ms that allow paper running, time when compiling excluded
     */
    protected long timeLimit;

    /**
     * init a test
     *
     * @param stdInput       data of the std input
     * @param stdOutput      data of the std output
     * @param compileCommand exactly compile command
     * @param runCommand     exactly run command
     * @param workingPath    path where test running
     * @param timeLimitMs    max running time in ms
     */
    public Test(String[] stdInput, String[] stdOutput, String compileCommand, String runCommand, String workingPath, long timeLimitMs) {
        this.input = stdInput;
        this.output = stdOutput;
        this.compileCmd = compileCommand;
        this.runCmd = runCommand;
        this.timeLimit = timeLimitMs;
        this.path = workingPath;
    }

    /**
     * run the test
     *
     * @return the test result
     * @throws TestException if sth wrong
     * @see TestResult
     */
    public TestResult run() throws TestException {
        try {
            if (!compile()) {
                return TestResult.CE;
            }
            String[] s = execute();
            if (check(s)) {
                return TestResult.AC;
            } else {
                return TestResult.WA;
            }
        } catch (TimeLimitExceedException e) {
            return TestResult.TLE;
        } catch (RuntimeErrorException e) {
            return TestResult.RE;
        }
    }

    /**
     * execute the paper
     *
     * @return answer, spilt in line separator
     * @throws TimeLimitExceedException if paper run more time than allowed
     * @throws RuntimeErrorException    if paper cannot finish normally
     */
    protected String[] execute() throws TimeLimitExceedException, RuntimeErrorException {
        String[] ans = null;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(runCmd, null, new File(path));
            OutputStream stdin = p.getOutputStream();
            Scanner stdout = new Scanner(p.getInputStream());
            Scanner stderr = new Scanner(p.getErrorStream());
            for (String s : input) {
                stdin.write(s.trim().getBytes());
                stdin.write(System.lineSeparator().getBytes());
                stdin.flush();
            }
            p.waitFor(timeLimit, TimeUnit.MILLISECONDS);
            if (p.isAlive()) {
                throw new TimeLimitExceedException();
            }
            if (stderr.hasNext() || p.exitValue() != 0) {
                throw new RuntimeErrorException();
            }
            List<String> list = new ArrayList<>();
            while (stdout.hasNext()) {
                list.add(stdout.nextLine());
            }
            return list.toArray(String[]::new);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw new RuntimeErrorException();
        } finally {
            if (p != null && p.isAlive()) {
                p.destroyForcibly();
            }
        }
    }

    /**
     * check the answer
     *
     * @param ans answer of paper
     * @return true if checker is OK, otherwise false
     */
    protected boolean check(String[] ans) {
        if (ans == null) {
            return false;
        }
        if (ans.length != output.length) {
            return false;
        }
        int len = output.length;
        for (int i = 0; i < len; i++) {
            if (!ans[i].trim().equals(output[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * compile the paper
     *
     * @return true if compiler finish normally, false otherwise
     */
    protected boolean compile() {
        try {
            Process p = Runtime.getRuntime().exec(compileCmd, null, new File(path));
            p.waitFor();
            if (p.exitValue() != 0) {
                return false;
            }
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
