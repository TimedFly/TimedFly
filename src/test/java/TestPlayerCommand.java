import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.api.Messenger;
import me.jackscode.timedfly.api.entity.TFPlayer;
import me.jackscode.timedfly.enums.CommandType;
import me.jackscode.timedfly.exceptions.CommandException;

public class TestPlayerCommand extends Command {

    public TestPlayerCommand() throws CommandException {
        super(
                "start",
                CommandType.TFLY,
                "Some command description",
                null
        );
    }

    @Override
    public void execute(Messenger sender, String[] args) {
        if (sender.isConsole()) {
            sender.sendMessage("Only players allowed");
            return;
        }
        TFPlayer player = sender.getTfPlayer();
        player.startTimer();
        sender.sendMessage("Successfully ran");
    }
}
