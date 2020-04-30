package me.jackscode.timedfly.commands;

import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.enums.CommandType;
import me.jackscode.timedfly.handlers.CommandHandler;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TF implements CommandExecutor {

    private final CommandHandler commandHandler;

    public TF(CommandHandler commandHandler) {
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
                .filter(cmd -> cmd.getCommandType() == CommandType.TIMED_FLY)
                .collect(Collectors.toList());

        if (commands.isEmpty()) return true;

        commands.stream()
                .filter(cmd -> cmd.getName().equals(args[0]))
                .forEach(cmd -> cmd.execute(sender, Arrays.copyOfRange(args, 1, args.length)));

        return true;
    }
}
