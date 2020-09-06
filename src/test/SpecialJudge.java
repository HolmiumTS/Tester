package test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * test in special judge mode
 * check the answer with the checker defined by user
 *
 * @author holmium
 * @see Test
 * @see Communicate
 */
public class SpecialJudge extends Test {
    /**
     * command to start the checker
     */
    protected final String checkCmd;

    /**
     * init a test
     *
     * @param stdInput          data of the std input
     * @param stdOutput         data of the std output
     * @param compileCommand    exactly compile command
     * @param runCommand        exactly run command
     * @param workingPath       path where test running
     * @param timeLimitMs       max running time in ms
     * @param runCheckerCommand start checker command
     */
    public SpecialJudge(String[] stdInput, String[] stdOutput, String compileCommand, String runCommand,
                        String workingPath, long timeLimitMs, String runCheckerCommand) {
        super(stdInput, stdOutput, compileCommand, runCommand, workingPath, timeLimitMs);
        this.input = stdInput;
        this.output = stdOutput;
        this.compileCmd = compileCommand;
        this.runCmd = runCommand;
        this.timeLimit = timeLimitMs;
        this.answer = null;
        this.path = workingPath;
        this.checkCmd = runCheckerCommand;
    }

    /**
     * check the answer with checker
     *
     * @param ans answer of paper
     * @return true if checker is OK, otherwise false
     * @see Test
     */
    @Override
    protected boolean check(String[] ans) {
        Process p = null;
        try {
            if (ans == null) {
                return false;
            }
            p = Runtime.getRuntime().exec(checkCmd);
            OutputStream stdin = p.getOutputStream();
//            System.out.println(path + " " + "input" + " " + Arrays.asList(input));
            for (String i : input) {
                stdin.write(i.trim().getBytes());
                stdin.write(System.lineSeparator().getBytes());
                stdin.flush();
            }
//            System.out.println(path + " " + "output" + " " + Arrays.asList(output));
            for (String o : output) {
                stdin.write(o.trim().getBytes());
                stdin.write(System.lineSeparator().getBytes());
                stdin.flush();
            }
//            System.out.println(path + " " + "ans" + " " + Arrays.asList(ans));
            for (String a : ans) {
                stdin.write(a.trim().getBytes());
                stdin.write(System.lineSeparator().getBytes());
                stdin.flush();
            }
            p.waitFor(timeLimit, TimeUnit.MILLISECONDS);
            if (p.isAlive()) {
                return false;
            }
            return p.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (p != null && p.isAlive()) {
                p.destroyForcibly();
            }
        }
    }

}
