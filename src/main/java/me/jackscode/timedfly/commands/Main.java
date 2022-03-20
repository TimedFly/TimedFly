package me.jackscode.timedfly.commands;

import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.api.Messenger;
import me.jackscode.timedfly.api.Messenger.OnClick;
import me.jackscode.timedfly.api.entity.TFConsole;
import me.jackscode.timedfly.api.entity.TFPlayer;
import me.jackscode.timedfly.enums.CommandType;
import me.jackscode.timedfly.handlers.CommandHandler;
import net.md_5.bungee.api.chat.ClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class Main implements CommandExecutor {

    private final CommandHandler commandHandler;

    public Main(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull org.bukkit.command.Command command,
            @NotNull String label,
            @NotNull String[] args) {

        final Messenger messenger;
        if (sender instanceof Player) {
            messenger = TFPlayer.getPlayer((Player) sender);
        } else {
            messenger = new TFConsole(Bukkit.getConsoleSender());
        }

        List<Command> commands = this.commandHandler
                .getCommands()
                .stream()
                .filter(cmd -> cmd.getCommandType() == CommandType.TIMED_FLY)
                .toList();

        if (commands.isEmpty()) {
            Bukkit.getServer().dispatchCommand(sender, "/tf help");
            return true;
        }

        if (args.length == 0 || args[0].equals("help")) {
            messenger.sendMessage("&c  TimedFly Help", "&6------------------");
            this.commandHandler.getCommands().stream().forEach(cmd -> cmd.sendHelpMessage(messenger));
            return true;
        }

        int[] count = { 0 };
        commands.stream()
                .filter(cmd -> cmd.getName().equals(args[0]))
                .forEach(cmd -> {
                    cmd.execute(messenger, Arrays.copyOfRange(args, 1, args.length));
                    count[0]++;
                });

        if (count[0] == 0) {
            messenger.sendHoverableMessage(
                    "&7Unrecognized command &3%s. &7Use &6/timedfly help.".formatted(args[0]),
                    new OnClick(ClickEvent.Action.RUN_COMMAND, "/tf help"),
                    "Click here to display the help message.");
        }

        return true;
    }
}
