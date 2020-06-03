package task;

public class CommandFormater {
    String command;
    int placeHolderNum;

    CommandFormater(String command) {
        this.command = command;
    }

    String formatCommand(String name) {
        return command.replaceAll("%s", name);
    }
}
