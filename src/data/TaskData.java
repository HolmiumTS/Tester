package data;

import test.Test;

import java.io.File;
import java.util.ArrayList;

public class TaskData {
  /**
   * get all test points
   *
   * @param path root dir of test data
   * @return test points under the path
   */

  public static TestPoint[] getTestPoint(String path) throws DataException {
    /**
     * 文件路径暂时设置为
     *├── question_num
     * │   ├── data
     * │   │   ├── in_1.txt
     * │   │   ├── out_1.txt
     * │   ├── command
     *         └── match.sh
     * command 是保留的文件夹，用于以后拓展自定义评测脚本的功能，现在的比对功能默认实现为全文比对
     * 对文件的要求，目录下必须有相应的 in_num.txt 和 out_num.txt ，也就是说data文件夹下的文件数目必须为偶数且in与
     * out数目相等，如果不等会抛出异常
     **/
    int pointNumber;
    File dir = new File(path);
    File[] list;
    ArrayList<TestPoint> testPoints = new ArrayList<TestPoint>();

    if (dir.isFile()) {
      throw new DataException("path point to a file");
    } else {
      list = Directory.local(dir, ".*");
      if (list.length % 2 != 0) {
        throw new DataException("lack of test files.");
      }
      pointNumber = list.length / 2;
      for (int i = 0; i <pointNumber ; i++) {
        testPoints.add(getOnePoint(i+1,list));
      }
    }
    return null;
  }

  private static TestPoint getOnePoint(int num,File[]files) throws DataException{
    int flag=0;
    TestPoint tmp=new TestPoint();
    for (File file : files) {
      if (file.getName().equals("in_"+num+".txt")){//get input

      }
      if (file.getName().equals("out_"+num+".txt")){//get output

      }
    }
    return tmp;
  }

  public static void main(String[] args) {
    System.out.println("in_"+1);
  } /**/
}
