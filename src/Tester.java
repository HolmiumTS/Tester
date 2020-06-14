import cli.Cli;
import com.beust.jcommander.JCommander;

/**
 * entrance of the tester
 *
 * @author Codevka
 */
public class Tester {
    // start app from here
    public static void main(String[] args) {
        Cli cli = new Cli();
        JCommander jCommander = JCommander.newBuilder().addObject(cli).build();
        jCommander.parse(args);
        cli.run(jCommander);
    }
}
