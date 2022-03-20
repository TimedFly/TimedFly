package me.jackscode.timedfly.commands;

import me.jackscode.timedfly.api.Argument;
import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.enums.CommandType;
import me.jackscode.timedfly.handlers.CommandHandler;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TabComplete implements org.bukkit.command.TabCompleter {

    private final CommandType commandType;
    private final List<Command> commands;
    private final CommandHandler commandHandler;

    public TabComplete(CommandHandler commandHandler, CommandType type) {
        this.commandHandler = commandHandler;
        this.commandType = type;
        this.commands = this.commandHandler.getCommands()
                .stream()
                .filter(cmd -> this.commandType == cmd.getCommandType())
                .toList();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command c, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        Optional<Command> optional = this.commands.stream()
                .filter(cmd -> cmd.getName().equals(args[0])).findFirst();

        if (args.length == 1) {
            List<String> commandsList = this.commands.stream().map(Command::getName).collect(Collectors.toList());
            commandsList.add("help");
            StringUtil.copyPartialMatches(args[0], commandsList, completions);
        } else if (args.length == 2) {

            if (optional.isPresent()) {
                StringUtil.copyPartialMatches(args[1],
                        optional.get().getArgs().keySet().stream().toList(),
                        completions);
            }

        } else if (args.length >= 3 && optional.isPresent()) {
            Command command = optional.get();
            List<Argument> argsList = command.getArgs().get(args[1]);
            if (argsList != null && args.length <= 3 + argsList.size()) {
                int index = args.length - 3;

                if (index >= 0 && index < argsList.size()) {
                    Argument argument = argsList.get(index);
                    List<String> tabComplete = argument.getTabComplete();

                    if (!tabComplete.isEmpty()) {
                        StringUtil.copyPartialMatches(args[2 + index], tabComplete, completions);
                    }
                }
            }
        }

        if (completions.isEmpty()) {
            List<String> players = Bukkit.getOnlinePlayers().stream().map(Player::getName)
                    .collect(Collectors.toList());
            players.add("*");
            StringUtil.copyPartialMatches(args[args.length - 1], players, completions);
        }

        Collections.sort(completions);

        return completions;
    }
}
