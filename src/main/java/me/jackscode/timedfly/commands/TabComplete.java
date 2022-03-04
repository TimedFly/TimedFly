package me.jackscode.timedfly.commands;

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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
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
        System.out.print(this.commands.size());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command c, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> commandsList = this.commands.stream().map(Command::getName).collect(Collectors.toList());
            commandsList.add("help");
            StringUtil.copyPartialMatches(args[0], commandsList, completions);
        } else if (args.length == 2) {
            Optional<Command> optional = this.commands.stream()
                    .filter(cmd -> cmd.getName().equals(args[0])).findFirst();

            if (!optional.isPresent()) {
                completions = new ArrayList<>();
            } else {
                StringUtil.copyPartialMatches(args[1],
                        optional.get().getArgs().keySet().stream().toList(),
                        completions);
            }

        } else if (args.length > 2) {
            List<String> players = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
            players.add("*");
            StringUtil.copyPartialMatches(args[args.length - 1], players, completions);
        }

        Collections.sort(completions);

        return completions;
    }
}
