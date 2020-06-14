package task;

/**
 * used to replace the place holder '%s' into paper's name
 *
 * @author holmium
 * @see Task
 */
public class CommandFormater {
    String command;
    int placeHolderNum;

    /**
     * @param command command that need to be format
     */
    CommandFormater(String command) {
        this.command = command;
    }

    String formatCommand(String name) {
        return command.replaceAll("%s", name);
    }
}
