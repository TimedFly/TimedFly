package me.jackint0sh.timedfly.commands;

import me.jackint0sh.timedfly.utilities.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private List<String> args;

    public TabCompleter(Arguments.Type type) {
        if (type.equals(Arguments.Type.TIMEDFLY))
            this.args = Arrays.stream(Arguments.TimedFly.values()).map(Arguments.TimedFly::toString).collect(Collectors.toList());
        else if (type.equals(Arguments.Type.TFLY))
            this.args = Arrays.stream(Arguments.TFly.values()).map(Arguments.TFly::toString).collect(Collectors.toList());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if ((sender.isOp()) || (sender.hasPermission(Permissions.ADMIN.getPermission()))) {
            List<String> completions = new ArrayList<>();

            if (args.length == 1) {
                StringUtil.copyPartialMatches(args[0], this.args, completions);
            } else if (args.length > 1) {
                List<String> players = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
                players.add("*");
                StringUtil.copyPartialMatches(args[args.length - 1], players, completions);
            }

            Collections.sort(completions);

            return completions;
        }
        return Collections.emptyList();
    }
}
