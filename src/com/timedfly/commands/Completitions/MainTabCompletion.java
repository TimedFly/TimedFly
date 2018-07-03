package com.timedfly.commands.Completitions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class MainTabCompletion implements TabCompleter {

    private static final String[] COMMANDS = {"help", "help2", "help3", "list", "setTime", "setPrice", "setItem",
            "reload", "permission", "update-config"};

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = FlyTabCompletion.getStrings(sender, args, COMMANDS);
        if (completions != null) return completions;

        return null;
    }
}
