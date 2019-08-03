package me.jackint0sh.timedfly.commands;

import me.jackint0sh.timedfly.flygui.inventories.FlightStore;
import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.utilities.Permissions;
import me.jackint0sh.timedfly.utilities.TimeParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TFly implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                try {
                    FlightStore.create((Player) sender);
                } catch (NullPointerException e) {
                    MessageUtil.sendError((Player) sender, e);
                }
            } else MessageUtil.sendConsoleMessage("&cOnly players can use this command!");
            return true;
        }

        switch (args[0]) {
            case "help":
            case "h":
                this.help(sender);
                break;
            case "add":
            case "a":
                if (args.length < 2) {
                    MessageUtil.sendMessage(sender, "&cUsage: " + Arguments.TFly.ADD.getUsage());
                    return true;
                }
                handleTimeArg(args, sender, true);
                break;
            case "set":
            case "s":
                if (args.length < 2) {
                    MessageUtil.sendMessage(sender, "&cUsage: " + Arguments.TFly.SET.getUsage());
                    return true;
                }
                handleTimeArg(args, sender, false);
                break;
            case "pause":
                toggleTimer(args, sender, 1);
                break;
            case "resume":
                toggleTimer(args, sender, 2);
                break;
            case "toggle":
                toggleTimer(args, sender, 3);
                break;
        }
        return true;
    }

    private void handleTimeArg(String[] args, CommandSender sender, boolean b) {
        Player player = Bukkit.getPlayerExact(args[args.length - 1]);
        int to = args.length - 1;
        if (TimeParser.isParsable(args[args.length - 1])) {
            if (!(sender instanceof Player)) {
                MessageUtil.sendMessage(sender, "Only players can do this.");
                return;
            }
            player = (Player) sender;
            to = args.length;
        } else if (player == null) {
            MessageUtil.sendError(sender, "That player is not online!");
            return;
        }

        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
        if (playerManager == null) {
            MessageUtil.sendError(player, "Something went wrong on line: " + new Throwable().getStackTrace()[0].getLineNumber());
            return;
        }

        if (player.equals(sender)) {
            if (b) {
                if (!PlayerManager.hasAnyPermission(player, Permissions.FLY_ADD_SELF, Permissions.FLY_ADD)) {
                    MessageUtil.sendNoPermission(player);
                    return;
                }
            } else {
                if (!PlayerManager.hasAnyPermission(player, Permissions.FLY_SET_SELF, Permissions.FLY_SET)) {
                    MessageUtil.sendNoPermission(player);
                    return;
                }
            }
        } else {
            if (b) {
                if (!PlayerManager.hasAnyPermission(player, Permissions.FLY_ADD_OTHERS, Permissions.FLY_ADD)) {
                    MessageUtil.sendNoPermission(sender);
                    return;
                }
            } else {
                if (!PlayerManager.hasAnyPermission(player, Permissions.FLY_SET_OTHERS, Permissions.FLY_SET)) {
                    MessageUtil.sendNoPermission(sender);
                    return;
                }
            }
        }

        try {
            String timeString = String.join("", Arrays.copyOfRange(args, 1, to));
            long time = TimeParser.parse(timeString);
            String text, from;

            if (b) {
                playerManager.addTime(time);
                text = "Time added successfully: " + timeString;
                from = "&c" + sender.getName() + "&7 added &e" + timeString + "&7 to your time.";
            } else {
                playerManager.setTime(time);
                text = "Time successfully set to: " + timeString;
                from = "&c" + sender.getName() + "&7 set your time to: &e" + timeString;
            }

            playerManager.startTimer();

            if (!player.equals(sender)) MessageUtil.sendMessage(player, from);

            MessageUtil.sendMessage(player, text);
        } catch (TimeParser.TimeFormatException e) {
            MessageUtil.sendError(player, e);
        }
    }

    private void toggleTimer(String[] args, CommandSender sender, int type) {
        Player player;
        if (args.length > 1) {
            player = Bukkit.getPlayerExact(args[args.length - 1]);
            if (player == null) {
                MessageUtil.sendError(sender, "That player is not online!");
                return;
            }
        } else {
            if (!(sender instanceof Player)) {
                MessageUtil.sendMessage(sender, "Only players can do this.");
                return;
            }
            player = (Player) sender;
        }

        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
        if (playerManager == null) {
            MessageUtil.sendError(player, "Something went wrong on line: " + new Throwable().getStackTrace()[0].getLineNumber());
            return;
        }

        if (player.equals(sender)) {
            if (!PlayerManager.hasAnyPermission(player, Permissions.FLY_TOGGLE_SELF, Permissions.FLY_TOGGLE_SELF)) {
                MessageUtil.sendNoPermission(player);
                return;
            }
        } else {
            if (!PlayerManager.hasAnyPermission(player, Permissions.FLY_TOGGLE_OTHERS, Permissions.FLY_TOGGLE)) {
                MessageUtil.sendNoPermission(sender);
                return;
            }
        }

        if (playerManager.getTimeLeft() < 1) {
            if (playerManager.isTimePaused()) MessageUtil.sendMessage(player, "The timer in not running...");
            return;
        }

        if (type == 1) playerManager.pauseTimer();
        else if (type == 2) playerManager.resumeTimer();
        else if (type == 3) {
            if (playerManager.isTimePaused()) playerManager.resumeTimer();
            else playerManager.pauseTimer();
        }

        if (playerManager.isTimePaused()) MessageUtil.sendMessage(player, "The timer has been paused!");
        else MessageUtil.sendMessage(player, "The timer has been resumed!");

    }

    private void help(CommandSender sender) {
        String timedFlyPre = MessageUtil.COMMAND_HELP_PREFIX + MessageUtil.COMMAND_TIMEDFLY;
        String tFlyPre = MessageUtil.COMMAND_HELP_PREFIX + MessageUtil.COMMAND_TFLY;

        MessageUtil.sendMessage(sender, MessageUtil.DIVIDER, false);
        MessageUtil.sendCenteredMessage(sender, MessageUtil.PLUGIN_NAME, MessageUtil.DIVIDER.length());

        List<String> commands = Arrays.stream(Arguments.TFly.values())
                .map(argument -> timedFlyPre + "&e" + argument.getUsage() + " &7- " + argument.getDescription())
                .collect(Collectors.toList());

        MessageUtil.sendMessages(sender, commands, false);
        MessageUtil.sendMessage(sender, tFlyPre + Arguments.TimedFly.HELP.getUsage() + " &7- " + Arguments.TimedFly.HELP.getDescription(), false);
        MessageUtil.sendMessage(sender, MessageUtil.DIVIDER, false);
    }
}
