package cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import task.Task;
import task.TaskException;

import java.util.ArrayList;
import java.util.List;

public class Cli {
    private static final String VERSION = "Java Tester v1.0";

    @Parameter(names = {"-v", "--version"}, description = "Java Tester v1.0", help = true, order = 6)
    private boolean version;

    @Parameter(names = {"-h", "--help"}, description = "Show help information", help = true, order = 5)
    private boolean help;

    @Parameter(names = {"-p", "--path"}, description = "Set root dir of the task", help = true, order = 4)
    private String root;

    public void run(JCommander jCommander) {
        if (help) {
            jCommander.setProgramName("Java Tester");
            jCommander.usage();
            return;
        }
        if (version) {
            jCommander.getConsole().println(VERSION);
            return;
        }

        if (root == null)
            root = getClass().getResource("/").toString().substring(5);

        Task task = new Task(root);
        try {
            task.run();
        } catch (TaskException e) {
            System.out.println(e.getMessage());
        }
    }
}