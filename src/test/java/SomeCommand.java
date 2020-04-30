import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.api.Messenger;
import me.jackscode.timedfly.enums.CommandType;
import me.jackscode.timedfly.exceptions.CommandException;

import java.util.Arrays;

public class SomeCommand extends Command {

    public SomeCommand() throws CommandException {
        super(
                "some",
                CommandType.TFLY,
                "Some command description",
                null
        );
    }

    @Override
    public void execute(Messenger sender, String[] args) {
        sender.sendMessage(String.format(
                "Here is some info about this command: \n" +
                        "Name: %s\nDescription:%s\nType: %s\nAliases: %s\nArguments: %s",
                this.getName(),
                this.getDescription(),
                this.getCommandType().name(),
                Arrays.toString(this.getAliases().toArray()),
                Arrays.toString(args)
        ));
        sender.sendMessage(Arrays.toString(args));
    }
}
