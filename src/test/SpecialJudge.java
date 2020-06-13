package test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class SpecialJudge extends Test {

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

    @Override
    protected boolean check(String[] ans) {
        Process p = null;
        try {
            if (ans == null) {
                return false;
            }
            p = Runtime.getRuntime().exec(checkCmd);
            OutputStream stdin = p.getOutputStream();
            for (String i : input) {
                stdin.write(i.getBytes());
                stdin.write(System.lineSeparator().getBytes());
                stdin.flush();
            }
            for (String o : output) {
                stdin.write(o.getBytes());
                stdin.write(System.lineSeparator().getBytes());
                stdin.flush();
            }
            for (String a : ans) {
                stdin.write(a.getBytes());
                stdin.write(System.lineSeparator().getBytes());
                stdin.flush();
            }
            p.waitFor(timeLimit, TimeUnit.MILLISECONDS);
            return true;
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
