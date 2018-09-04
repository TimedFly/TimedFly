package com.timedfly.utilities;

import com.timedfly.NMS.NMS;
import com.timedfly.configurations.ConfigCache;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Message {

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void sendConsoleMessage(String message) {
        sendMessage(Bukkit.getServer().getConsoleSender(), message);
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(color(ConfigCache.getPrefix() + message));
    }

    public static void sendMessage(Player player, String message) {
        player.sendMessage(color(ConfigCache.getPrefix() + message));
    }

    public static void sendNoPermission(Player player, FileConfiguration languageConfig, NMS nms) {
        sendMessage(player, languageConfig.getString("Other.NoPermission.Message"));
        nms.sendTitle(player, color(languageConfig.getString("Other.NoPermission.Title")), 5, 40, 5);
        nms.sendSubtitle(player, color(languageConfig.getString("Other.NoPermission.SubTitle")), 5, 40, 5);
        nms.sendActionbar(player, color(languageConfig.getString("Other.NoPermission.Message")));
    }

    public static void sendDisabledWorld(Player player, FileConfiguration languageConfig) {
        sendMessage(player, languageConfig.getString("Other.DisabledWorld"));
    }

    public static void sendDisabledWorld(CommandSender sender, FileConfiguration languageConfig) {
        sendMessage(sender, languageConfig.getString("Other.DisabledWorld"));
    }

    public static void sendNoMoney(Player player, FileConfiguration languageConfig, int price, int time) {
        sendMessage(player, languageConfig.getString("Fly.Message.NoMoney").replace("%price%", Integer.toString(price))
                .replace("%time%", Integer.toString(time)));
    }

    public static void sendDebugMessage(String message, int level) {
        if (!ConfigCache.isDebug()) return;
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.isOp()) sendMessage(player, "&6" + message);
        });
    }
}
