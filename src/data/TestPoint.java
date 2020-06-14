package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * package one test point's data
 * used to transfer data
 *
 * @author AI
 * @see test.Test
 */
public class TestPoint {

    String[] input;
    String[] output;
    int name;

    public void setName(int name) {
        this.name = name;
    }

    public void setInput(File file) throws FileNotFoundException {
        Scanner input = new Scanner(file);
        ArrayList<String> strings = new ArrayList<String>();
        while (input.hasNext()) {
            strings.add(input.nextLine());
        }
        this.input = new String[strings.size()];
        this.input = strings.toArray(this.input);
        input.close();
    }

    public void setOutput(File file) throws FileNotFoundException {
        Scanner input = new Scanner(file);
        ArrayList<String> strings = new ArrayList<String>();
        while (input.hasNext()) {
            strings.add(input.nextLine());
        }
        this.output = new String[strings.size()];
        this.output = strings.toArray(this.output);
        input.close();
    }

    /**
     * get std input of this test point
     *
     * @return std input lines
     */
    public String[] getInput() {
        return this.input;
    }

    /**
     * get std output of this test point
     *
     * @return std output lines
     */
    public String[] getOutput() {
        return this.output;
    }

    /**
     * get this test point name
     *
     * @return test point name
     */
    public String getName() {
        return "testPoint" + this.name;
    }
}
