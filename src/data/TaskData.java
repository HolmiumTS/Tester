package data;

import test.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * get all task data
 *
 * @author AI
 * @see TestPoint
 */
public class TaskData {
    /**
     * get all test points
     * <p>
     * 文件路径设置为
     * ├── question_num
     * │   ├── data
     * │   │   ├── in_1.txt
     * │   │   ├── out_1.txt
     * │   ├── command
     *         └── match.sh
     * <p>
     * 对文件的要求，目录下必须有相应的 in_num.txt 和 out_num.txt ，也就是说data文件夹下的文件数目必须为偶数且in与
     * out数目相等，如果不等会抛出异常
     * <p>
     * point id must be continuous, and start from 1
     *
     * @param path root dir of test data
     * @return test points under the path
     * @throws DataException 对文件的要求，目录下必须有相应的 in_num.txt 和 out_num.txt ，也就是说data文件夹下的文件数目必须为偶数且in,out数目相等，如果不等会抛出异常
     */

    public static TestPoint[] getTestPoint(String path) throws DataException {

        int pointNumber;
        File dir = new File(path);
        File[] list;
        ArrayList<TestPoint> testPoints = new ArrayList<TestPoint>();
        TestPoint[] testPoints1;
        if (dir.isFile()) {
            throw new DataException("path point to a file");
        } else {
            list = Directory.local(dir, ".*");
            if (list.length % 2 != 0) {
                throw new DataException("lack of test files.");
            }
            pointNumber = list.length / 2;
            for (int i = 0; i < pointNumber; i++) {
                testPoints.add(getOnePoint(i + 1, list));
            }
        }
        testPoints1 = new TestPoint[testPoints.size()];
        testPoints1 = testPoints.toArray(testPoints1);
        return testPoints1;
    }

    /**
     * get one point which id is num
     *
     * @param num   point id
     * @param files all test files
     * @return
     * @throws DataException
     */
    private static TestPoint getOnePoint(int num, File[] files) throws DataException {
        int flag = 0;
        TestPoint tmp = new TestPoint();
        try {
            tmp.setName(num);
            for (File file : files) {
                if (file.getName().equals("in_" + num + ".txt")) {
                    // get input
                    tmp.setInput(file);
                }
                if (file.getName().equals("out_" + num + ".txt")) {
                    // get output
                    tmp.setOutput(file);
                }
            }
            return tmp;
        } catch (FileNotFoundException e) {
            throw new DataException("File not found.");
        }
    }

    /**
     * use to test if this part work
     */
    public static void main(String[] args) {
        try {
            TestPoint[] testPoints = getTestPoint("/home/ai/IdeaProjects/Tester/src/toPlaceTest/question_1/data/");
            for (TestPoint testPoint : testPoints) {
                System.out.println(testPoint.getName());
                for (String input : testPoint.getInput()) {
                    System.out.println(input);
                }
                for (String output : testPoint.getOutput()) {
                    System.out.println(output);
                }
            }
        } catch (DataException e) {
            System.out.println(e.error);
        }
    }
}
