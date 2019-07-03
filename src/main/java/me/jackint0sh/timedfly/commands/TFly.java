package me.jackint0sh.timedfly.commands;

import me.jackint0sh.timedfly.flygui.inventories.FlightStore;
import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.utilities.TimeParser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TFly implements CommandExecutor {

    private Plugin plugin;

    public TFly(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player)
                try {
                    FlightStore.create((Player) sender);
                } catch (NullPointerException e) {
                    MessageUtil.sendError((Player) sender, e);
                }
            else MessageUtil.sendConsoleMessage("&cOnly players can use this command!");
            return true;
        }

        switch (args[0]) {
            case "help":
            case "h":
                this.help(sender);
                break;
            case "set":
            case "s":
                if (args.length < 2) {
                    MessageUtil.sendMessage(sender, "&cUsage: " + Arguments.TFly.SET.getUsage());
                    return true;
                }
                Player player = Bukkit.getPlayerExact(args[args.length - 1]);
                int to = args.length - 1;
                if (player == null /*|| TimeParser.isTimeString(args[args.length - 1])*/) {
                    player = (Player) sender;
                    to = args.length;
                }

                PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
                try {
                    int time = TimeParser.toTicks(String.join("", Arrays.copyOfRange(args, 1, to)));
                    playerManager.setTime(time);
                    MessageUtil.sendMessage(player, time + "");
                } catch (TimeParser.TimeFormatException e) {
                    MessageUtil.sendError(player, e);
                }
                break;
        }
        return true;
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
