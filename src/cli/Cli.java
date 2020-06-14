package cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import task.Task;
import task.TaskException;

import java.util.ArrayList;
import java.util.List;

/**
 * command line core
 * use to show correct params
 * @see JCommander
 * @see Parameter
 * @see Task
 * @author Codevka
 */
public class Cli {
    private static final String VERSION = "Java Tester v1.0";

    @Parameter(names = {"-v", "--version"}, description = "Java Tester v1.0", help = true, order = 6)
    private boolean version;

    @Parameter(names = {"-h", "--help"}, description = "Show help information", help = true, order = 5)
    private boolean help;

    @Parameter(names = {"-p", "--path"}, description = "Set root dir of the task", help = true, order = 4)
    private String root;

    /**
     * execute command
     * @param jCommander command need to be execute
     */
    public void run(JCommander jCommander) {
        // show help
        if (help) {
            jCommander.setProgramName("Java Tester");
            jCommander.usage();
            return;
        }
        // shw version
        if (version) {
            jCommander.getConsole().println(VERSION);
            return;
        }
        // get default root
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