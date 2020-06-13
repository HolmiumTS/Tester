package test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class Communicate extends SpecialJudge {
    volatile TestResult result;

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
    public Communicate(String[] stdInput, String[] stdOutput, String compileCommand, String runCommand, String workingPath, long timeLimitMs, String runCheckerCommand) {
        super(stdInput, stdOutput, compileCommand, runCommand, workingPath, timeLimitMs, runCheckerCommand);
    }

    @Override
    public TestResult run() throws TestException {
        if (!compile()) {
            return TestResult.CE;
        }
        Thread ec = new Thread(this::executeAndCheck);
        ec.start();
        try {
            ec.join(timeLimit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (ec.isAlive()) {
            ec.interrupt();
        }
        return result;
    }

    private void executeAndCheck() {
        Process a = null;
        Process c = null;
        try {
            c = Runtime.getRuntime().exec(checkCmd);
            OutputStream cin = c.getOutputStream();
            a = Runtime.getRuntime().exec(runCmd, null, new File(path));
            for (String i : input) {
                cin.write(i.getBytes());
                cin.write(System.lineSeparator().getBytes());
                cin.flush();
            }
            for (String o : output) {
                cin.write(o.getBytes());
                cin.write(System.lineSeparator().getBytes());
                cin.flush();
            }
            Scanner cout = new Scanner(c.getInputStream());
            OutputStream ain = a.getOutputStream();
            Scanner aout = new Scanner(a.getInputStream());
            while (a.isAlive() && c.isAlive()) {
                if (Thread.currentThread().isInterrupted()) {
                    result = TestResult.TLE;
                    return;
                }
                while (aout.hasNextLine()) {
                    cin.write(aout.nextLine().getBytes());
                    cin.write(System.lineSeparator().getBytes());
                    cin.flush();
                }
                while (cout.hasNextLine()) {
                    ain.write(aout.nextLine().getBytes());
                    ain.write(System.lineSeparator().getBytes());
                    ain.flush();
                }
            }
            a.waitFor();
            c.waitFor();
            if (a.exitValue() != 0) {
                result = TestResult.RE;
            } else if (c.exitValue() != 0) {
                result = TestResult.WA;
            } else {
                result = TestResult.AC;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = TestResult.RE;
        } catch (InterruptedException e) {
            e.printStackTrace();
            result = TestResult.TLE;
        } finally {
            if (a != null && a.isAlive()) {
                a.destroyForcibly();
            }
            if (c != null && c.isAlive()) {
                c.destroyForcibly();
            }
        }
    }
}
