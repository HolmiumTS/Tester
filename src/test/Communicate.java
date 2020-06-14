package test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * test in communicate node
 * the checker can communicate with paper's program dynamically
 *
 * @see SpecialJudge
 * @see Test
 * @see task.TaskType
 */
public class Communicate extends SpecialJudge {
    /**
     * use volatile keyword because we will get and set it concurrently
     */
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

    /**
     * start to execute the test
     *
     * @return
     * @throws TestException
     * @see Test
     */
    @Override
    public TestResult run() throws TestException {
        // compile failed
        if (!compile()) {
            return TestResult.CE;
        }
        // create a new thread to run executeAndCheck function
        Thread ec = new Thread(this::executeAndCheck);
        ec.start();
        try {
            // wait ec to die
            ec.join(timeLimit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // ec still not die, interrupt it
        if (ec.isAlive()) {
            ec.interrupt();
//            try {
//                ec.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            result = TestResult.TLE;
        }
        return result;
    }

    /**
     * execute paper and check it at the same time
     */
    private void executeAndCheck() {
        Process a = null;
        Process c = null;
        try {
            // checker
            c = Runtime.getRuntime().exec(checkCmd);
            OutputStream cin = c.getOutputStream();
            // paper
            a = Runtime.getRuntime().exec(runCmd, null, new File(path));
            // transfer std input and std output to checker
            System.out.println(path + " " + "input" + " " + Arrays.asList(input));
            for (String i : input) {
                cin.write(i.trim().getBytes());
                cin.write(System.lineSeparator().getBytes());
                cin.flush();
            }
            System.out.println(path + " " + "output" + " " + Arrays.asList(output));
            for (String o : output) {
                cin.write(o.trim().getBytes());
                cin.write(System.lineSeparator().getBytes());
                cin.flush();
            }
            InputStream cout = c.getInputStream();
            OutputStream ain = a.getOutputStream();
            InputStream aout = a.getInputStream();
            // let checker and paper communicate
            while (a.isAlive() && c.isAlive()) {
                if (Thread.currentThread().isInterrupted()) {
                    result = TestResult.TLE;
                    return;
                }
                while (aout.available() != 0) {
                    cin.write(aout.read());
                    cin.flush();
                }
                while (cout.available() != 0) {
                    ain.write(cout.read());
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
//            e.printStackTrace();
            result = TestResult.RE;
        } catch (InterruptedException e) {
//            e.printStackTrace();
            result = TestResult.TLE;
        } finally {
            // clean checker and paper
            if (a != null && a.isAlive()) {
                a.destroyForcibly();
            }
            if (c != null && c.isAlive()) {
                c.destroyForcibly();
            }
        }
    }
}
