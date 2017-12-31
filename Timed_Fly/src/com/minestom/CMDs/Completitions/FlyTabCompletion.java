package com.minestom.CMDs.Completitions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlyTabCompletion implements TabCompleter {

    private static final String[] COMMANDS = {"set", "timeleft", "on", "off", "add", "help", "resume", "stop"};

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if ((sender.isOp()) || (sender.hasPermission("timedfly.admin"))) {
            List completions = new ArrayList();

            if (args.length == 1) {
                String partialCommand = args[0];
                List commands = new ArrayList(Arrays.asList(COMMANDS));
                StringUtil.copyPartialMatches(partialCommand, commands, completions);
            }

            if (args.length == 2) {
                String players = args[1];
                List<String> playerName = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    playerName.add(player.getName());
                }
                StringUtil.copyPartialMatches(players, playerName, completions);
            }

            Collections.sort(completions);

            return completions;
        }

        return null;
    }

}
