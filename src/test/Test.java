package test;

import java.io.*;
import java.util.ArrayList;

public class Test {
    /**
     * @// TODO: 2020/6/5 1.执行编译指令 compileCommand 生成对应的文件 {done}
     * 2.执行 runCommand 将输出结果与 stdOutput比对
     * 3.返回相应的结果
     */
    String[] input;
    String[] stdOutput;
    String compileCMD;
    String runCMD;
    long timeLimit;
    String defaultPath = ".";
    Result result;
    static ArrayList<String> Poutput;
    String[] ProOutput;
    static boolean programEndedNormally = false;

    public static void callback(ArrayList<String> output) {
        Poutput = output;
        programEndedNormally = true;
    }

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
        this.stdOutput = stdOutput;
        this.compileCMD = compileCommand;
        this.runCMD = runCommand;
        this.timeLimit = timeLimitMs;
        this.result = new Result();
    }

    /**
     * 守护线程，用来实现超时退出功能
     */
    static class Daemon implements Runnable {
        private Thread thread;
        private long time;

        Daemon(Thread th, long t) {
            thread = th;
            time = t;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    //do nothing
                }
                thread.interrupt();
            }
        }
    }

    /**
     * 任务线程
     */
    class Task extends Thread {
        String[] input;
        String[] stdOutput;
        String runCMD;
        String path;

        /**
         * @// TODO: 2020/6/6 1.处理读入输出的问题 2.进行文本匹配
         */
        Task(String[] input, String[] stdOutput, String runCMD, String path) {
            this.input = input;
            this.stdOutput = stdOutput;
            this.runCMD = runCMD;
            this.path = path;
        }

    @Override
    public void run() {
      Process process = null;
      BufferedReader buffIn = null;
      BufferedWriter buffOut = null;
      String string;
      try {
        sleep(1);
        process = Runtime.getRuntime().exec(runCMD, null, new File(path));
        ArrayList<String> output = new ArrayList<>();
        buffIn = new BufferedReader(new InputStreamReader(process.getInputStream()));
        PrintWriter printWriter = new PrintWriter(process.getOutputStream());
        for (String s : input) {
          printWriter.println(s);
        }
        printWriter.flush();
        while ((string = buffIn.readLine()) != null) {
          output.add(string);
        }
        callback(output);
        buffIn.close();
//        buffOut.close();
      } catch (IOException e) {
        result.setResult(TestResult.RE);
      } catch (InterruptedException e) {
        result.setResult(TestResult.TLE);
      } finally {
        if (process != null) {
          process.destroy();
        }
      }
    }
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
      execCompile();
      execRun(this.timeLimit);
      if (programEndedNormally) {
        ProOutput = new String[Poutput.size()];
        Poutput.toArray(ProOutput);
        if (new DefaultTest().matchWay(ProOutput, stdOutput)) {
          result.setResult(TestResult.AC);
        } else {
          result.setResult(TestResult.WA);
        }
      }
    } catch (CompileErrorException e) {
      result.setResult(TestResult.CE);
    } catch (Exception e) {
      result.setResult(TestResult.RE);
    }
    return this.result.getResult();
  }

  /**
   * 这段代码实现了对每个测试的超时处理，并开始了测试线程
   * 测试的时间只包括了程序运行的时间,不包括编译时间,文本比对时间
   * 在程序运行结束以后系统调用回调函数返回程序输出的句柄
   */
  public void execRun(long timeLimit) {
    long startTime = System.currentTimeMillis();

    Task runThr = new Task(this.input, this.stdOutput, this.runCMD, this.defaultPath);//设置运行线程

    Daemon daemon = new Daemon(runThr, timeLimit);
    Thread daemonThread = new Thread(daemon);//设置守护线程
    daemonThread.setDaemon(true);

    runThr.start();
    daemonThread.start();
  }

  /**
   * 传入cmd和调用时默认的javac路径
   * 进行编译，编译成功无任何返回，否则会直接抛出异常被上层catch
   */
  public void execCompile() throws Exception {
    Process process = null;
    BufferedReader buffErr = null;
    StringBuilder result = new StringBuilder();
    try {
      process = Runtime.getRuntime().exec(this.compileCMD, null, new File(this.defaultPath));
      process.waitFor(); //等待指令完成
      buffErr = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8")); //建立reader指向标准错误流
      String line = null;
      if ((line = buffErr.readLine()) != null) {//如果！=null说明编译出错了

        result.append(line).append('\n');//
        while ((line = buffErr.readLine()) != null) {
          result.append(line).append('\n');
        }
        System.out.println(result);
        //这一段用于输出编译错误原因，如果不需要可以直接删除

        throw new CompileErrorException();//抛出异常
      }
    } finally {
      closeStream(buffErr);
      if (process != null) {
        process.destroy();
      }//关闭流和子进程
    }
  }

  private static void closeStream(Closeable stream) {//关闭流
    if (stream != null) {
      try {
        stream.close();
      } catch (Exception e) {
        // nothing
      }
    }
  }
}
