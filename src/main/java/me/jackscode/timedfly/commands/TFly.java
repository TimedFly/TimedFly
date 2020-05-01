package me.jackscode.timedfly.commands;

import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.api.Messenger;
import me.jackscode.timedfly.api.entity.TFConsole;
import me.jackscode.timedfly.api.entity.TFPlayer;
import me.jackscode.timedfly.enums.CommandType;
import me.jackscode.timedfly.handlers.CommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TFly implements CommandExecutor {

    private final CommandHandler commandHandler;

    public TFly(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull org.bukkit.command.Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (args.length == 0) return true;

        List<Command> commands = this.commandHandler
                .getCommands()
                .stream()
                .filter(cmd -> cmd.getCommandType() == CommandType.TFLY)
                .collect(Collectors.toList());

        if (commands.isEmpty()) return true;

        final Messenger messenger;
        if (sender instanceof Player) {
            messenger = new TFPlayer(((Player) sender));
        } else {
            messenger = new TFConsole(Bukkit.getConsoleSender());
        }

        commands.stream()
                .filter(cmd -> cmd.getName().equals(args[0]))
                .forEach(cmd -> cmd.execute(messenger, Arrays.copyOfRange(args, 1, args.length)));

        return true;
    }
}
