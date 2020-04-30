import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.api.TFPlayer;
import me.jackscode.timedfly.enums.CommandType;
import me.jackscode.timedfly.exceptions.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
    public void execute(CommandSender sender, String[] args) {
        TFPlayer player = new TFPlayer(((Player) sender).getUniqueId());
        player.startTimer();
    }
}
