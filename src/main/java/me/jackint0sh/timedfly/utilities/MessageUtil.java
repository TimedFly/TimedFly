package me.jackint0sh.timedfly.utilities;

import me.jackint0sh.timedfly.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MessageUtil {

    public static String COMMAND_HELP_PREFIX = "&a» ";
    public static String COMMAND_TIMEDFLY = "&e/timedfly ";
    public static String COMMAND_TFLY = "&e/tfly ";
    public static String DIVIDER = "&6&l------------------------------";
    public static String PLUGIN_NAME = "&c&lTimedFly";
    public static String PLUGIN_PREFIX = "&c&lTimedFly > &7";

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String replacePlaceholders(Player player, String text) {
        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
        String timeLeft = playerManager.getTimeLeft() + "";
        String initialTime = playerManager.getInitialTime() + "";

        if (timeLeft.equals("0") || timeLeft.equals("-1")) timeLeft = "No Time";
        if (initialTime.equals("0") || initialTime.equals("-1")) timeLeft = "No Time";

        return MessageUtil.color(text)
                .replace("[player_name]", player.getName())
                .replace("[time_left]", timeLeft)
                .replace("[initial_time]", initialTime)
                .replace("[vault_balance]", "0")
                .replace("[player_points_balance]", "0")
                .replace("[tokens_manager_balance]", "0")
                .replace("[levels_balance]", "0")
                .replace("[exp_balance]", "0")
                ;
    }

    public static List<String> replacePlaceholders(Player player, List<String> text) {
        return text.stream().map(m -> MessageUtil.replacePlaceholders(player, m)).collect(Collectors.toList());
    }

    public static void sendMessage(CommandSender to, String text, boolean prefix) {
        to.sendMessage(color((prefix ? PLUGIN_PREFIX : "") + text));
    }

    public static void sendMessage(CommandSender to, String text) {
        sendMessage(to, text, true);
    }

    public static void sendMessages(CommandSender to, String[] text, boolean prefix) {
        Arrays.stream(text).forEach(message -> sendMessage(to, message, prefix));
    }

    public static void sendMessages(CommandSender to, String[] text) {
        sendMessages(to, text, true);
    }

    public static void sendMessages(CommandSender to, List<String> text, boolean prefix) {
        text.forEach(message -> {
            sendMessage(to, message, prefix);
            System.out.println(message);
        });
    }

    public static void sendMessages(CommandSender to, List<String> text) {
        sendMessages(to, text, true);
    }

    public static void sendMessage(Player to, String text, boolean prefix) {
        to.sendMessage(color((prefix ? PLUGIN_PREFIX : "") + text));
    }

    public static void sendMessage(Player to, String text) {
        sendMessage(to, text, true);
    }

    public static void sendMessages(Player to, String[] text, boolean prefix) {
        Arrays.stream(text).forEach(message -> sendMessage(to, message, prefix));
    }

    public static void sendMessages(Player to, String[] text) {
        sendMessages(to, text, true);
    }

    public static void sendMessages(Player to, List<String> text, boolean prefix) {
        text.forEach(message -> sendMessage(to, message, prefix));
    }

    public static void sendMessages(Player to, List<String> text) {
        sendMessages(to, text, true);
    }

    public static void sendConsoleMessage(String text) {
        sendMessage(Bukkit.getConsoleSender(), text);
    }

    public static void sendConsoleMessages(List<String> text) {
        sendMessages(Bukkit.getConsoleSender(), text);
    }

    public static void sendError(Player player, String text) {
        sendMessage(player, "&c" + text);
        sendError(text);
    }

    public static void sendError(Player player, Exception e) {
        sendMessage(player, "&c" + e.getMessage());
        if (me.jackint0sh.timedfly.TimedFly.debug) e.printStackTrace();
        else sendError(e.getMessage());
    }

    public static void sendError(CommandSender to, String text) {
        sendMessage(to, "&c" + text);
        sendError(text);
    }

    public static void sendError(CommandSender to, Exception e) {
        sendMessage(to, "&c" + e.getMessage());
        if (me.jackint0sh.timedfly.TimedFly.debug) e.printStackTrace();
        else sendError(e.getMessage());
    }

    public static void sendError(String text) {
        sendMessage(Bukkit.getConsoleSender(), "&c&lTimedFly ERROR > &7" + text, false);
    }

    public static void sendNoPermission(CommandSender to) {
        sendMessage(to, "&cYou don't have permission to do this!", true);
    }

    public static void sendNoPermission(Player to) {
        sendMessage(to, "&cYou don't have permission to do this!", true);
    }

    public static void sendCenteredMessage(CommandSender to, String text, int width) {
        StringBuilder stringBuilder = new StringBuilder();
        int amount = (width - text.length()) / 2;

        IntStream.of(amount).forEach((i) -> stringBuilder.append(" "));

        sendMessage(to, stringBuilder.append(text).toString(), false);
    }
}
