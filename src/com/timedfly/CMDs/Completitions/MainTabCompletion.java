package com.timedfly.CMDs.Completitions;

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

public class MainTabCompletion implements TabCompleter {

    private static final String[] COMMANDS = {"help", "help2", "help3", "list", "setTime", "setPrice", "setItem", "reload", "permission"};

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List completions = FlyTabCompletion.getStrings(sender, args, COMMANDS);
        if (completions != null) return completions;

        return null;
    }
}
