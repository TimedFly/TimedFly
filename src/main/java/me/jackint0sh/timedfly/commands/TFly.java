package me.jackint0sh.timedfly.commands;

import me.jackint0sh.timedfly.flygui.inventories.FlightStore;
import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.utilities.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
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
                    if (!Config.getConfig("config").get().getBoolean("Gui.Enable")) {
                        MessageUtil.sendTranslation(sender, "error.disabled.gui");
                        return true;
                    }
                    FlightStore.create((Player) sender);
                } catch (NullPointerException e) {
                    MessageUtil.sendError((Player) sender, e);
                }
            } else MessageUtil.sendTranslation(sender, "error.player.not_player");
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
                    MessageUtil.sendTranslation(sender, "error.usage", new String[][]{{"[usage]", Arguments.TFly.ADD.getUsage()}});
                    return true;
                }
                if (args[args.length - 1].equals("*")) {
                    Bukkit.getOnlinePlayers().forEach(player -> handleTimeArg(args, player, true));
                    return true;
                }
                handleTimeArg(args, sender, true);
                break;
            case "set":
            case "s":
                if (args.length < 2) {
                    MessageUtil.sendTranslation(sender, "error.usage", new String[][]{{"[usage]", Arguments.TFly.SET.getUsage()}});
                    return true;
                }
                if (args[args.length - 1].equals("*")) {
                    Bukkit.getOnlinePlayers().forEach(player -> handleTimeArg(args, player, false));
                    return true;
                }
                handleTimeArg(args, sender, false);
                break;
            case "timeleft":
            case "tl":
                timeLeft(args, sender);
                break;
            case "pause":
                toggleTimer(args, sender, ToggleType.PAUSE);
                break;
            case "resume":
                toggleTimer(args, sender, ToggleType.RESUME);
                break;
            case "toggle":
                toggleTimer(args, sender, ToggleType.TOGGLE);
                break;
            default:
                MessageUtil.sendTranslation(sender, "error.not_found.command");
                break;
        }
        return true;
    }

    private void handleTimeArg(String[] args, CommandSender sender, boolean b) {
        int to = args.length - 1;
        Player player = Bukkit.getPlayerExact(args[to]);

        if (args[to].equals("*")) player = (Player) sender;
        if (TimeParser.isParsable(args[args.length - 1])) {
            if (!(sender instanceof Player)) {
                MessageUtil.sendTranslation(sender, "error.player.not_player");
                return;
            }
            player = (Player) sender;
            to = args.length;
        } else if (isPlayerNotOnline(player, sender)) return;

        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
        if (playerManager == null) {
            MessageUtil.sendTranslation(player, "error.unknown", new String[][]{{
                    "[line]", new Throwable().getStackTrace()[0].getLineNumber() + ""
            }});
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
                if (!PlayerManager.hasAnyPermission(sender, Permissions.FLY_ADD_OTHERS, Permissions.FLY_ADD)) {
                    MessageUtil.sendNoPermission(sender);
                    return;
                }
            } else {
                if (!PlayerManager.hasAnyPermission(sender, Permissions.FLY_SET_OTHERS, Permissions.FLY_SET)) {
                    MessageUtil.sendNoPermission(sender);
                    return;
                }
            }
        }

        try {
            String timeString = String.join("", Arrays.copyOfRange(args, 1, to));
            long time = TimeParser.parse(timeString);
            String self, other;

            if (b) {
                playerManager.addTime(time);
                self = "fly.time.add.self";
                other = "fly.time.add.other";
            } else {
                playerManager.setTime(time);
                self = "fly.time.set.self";
                other = "fly.time.set.other";
            }

            playerManager.startTimer();
            playerManager.setFromPlugin(true);

            if (!player.equals(sender)) MessageUtil.sendTranslation(player, other, new String[][]{
                    new String[]{"[user_name]", sender.getName()},
                    new String[]{"[time]", timeString},
                    new String[]{"[time_left]", playerManager.getTimeLeft() + ""}
            });

            MessageUtil.sendTranslation(sender, self, new String[][]{
                    new String[]{"[user_name]", sender.getName()},
                    new String[]{"[time]", timeString},
                    new String[]{"[time_left]", playerManager.getTimeLeft() + ""}
            });
        } catch (TimeParser.TimeFormatException e) {
            MessageUtil.sendError(player, e);
        }
    }

    static void toggleTimer(String[] args, CommandSender sender, ToggleType type) {
        Player player;
        if (args != null && args.length > 1) {
            player = Bukkit.getPlayerExact(args[args.length - 1]);
            if (isPlayerNotOnline(player, sender)) return;
        } else {
            if (!(sender instanceof Player)) {
                MessageUtil.sendTranslation(sender, "error.player.not_player");
                return;
            }
            player = (Player) sender;
        }

        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
        if (playerManager == null) {
            MessageUtil.sendTranslation(player, "error.unknown", new String[][]{{
                    "[line]", new Throwable().getStackTrace()[0].getLineNumber() + ""
            }});
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

        if (!playerManager.hasTime()) {
            MessageUtil.sendTranslation(player, "error.fly.not_running");
            return;
        }

        if (type.equals(ToggleType.PAUSE)) {
            playerManager.pauseTimer();
            playerManager.setFromPlugin(false);
        } else if (type.equals(ToggleType.RESUME)) {
            playerManager.resumeTimer();
            playerManager.setFromPlugin(true);
        } else if (type.equals(ToggleType.TOGGLE)) {
            if (playerManager.isTimePaused()) {
                playerManager.resumeTimer();
                playerManager.setFromPlugin(true);
            } else {
                playerManager.pauseTimer();
                playerManager.setFromPlugin(false);
            }
        }

        if (playerManager.isTimePaused()) MessageUtil.sendTranslation(player, "fly.time.toggle.pause");
        else MessageUtil.sendTranslation(player, "fly.time.toggle.resume");

    }

    private void timeLeft(String[] args, CommandSender sender) {
        Player player;
        if (args.length > 1) {
            player = Bukkit.getPlayerExact(args[1]);
            if (isPlayerNotOnline(player, sender)) return;

            PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

            String timeLeft = TimeParser.toReadableString(playerManager.getTimeLeft());
            if (timeLeft.isEmpty()) timeLeft = Languages.getString("fly.time.no_time");

            MessageUtil.sendTranslation(sender, "fly.time.time_left.others", new String[][]{
                    new String[]{"[target]", player.getName()},
                    new String[]{"[time_left]", timeLeft}
            });
        } else {
            if (sender instanceof Player) {
                player = (Player) sender;
                MessageUtil.sendTranslation(player, "fly.time.time_left.self");
            } else {
                MessageUtil.sendTranslation(sender, "error.usage", new String[][]{{"[usage]", Arguments.TFly.TIMELEFT.getUsage()}});
            }
        }

    }

    private static boolean isPlayerNotOnline(@Nullable Player player, CommandSender sender) {
        if (player == null) {
            MessageUtil.sendTranslation(sender, "error.player.not_online");
            return true;
        }

        return false;
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

    enum ToggleType {
        PAUSE, RESUME, TOGGLE
    }
}
