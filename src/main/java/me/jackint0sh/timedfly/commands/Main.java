package me.jackint0sh.timedfly.commands;

import me.jackint0sh.timedfly.flygui.FlyItem;
import me.jackint0sh.timedfly.flygui.FlyItemCreator;
import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.utilities.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 0) {
            help(sender);
        } else switch (args[0].toLowerCase()) {
            case "help":
            case "h":
                help(sender);
                break;
            case "reload":
            case "r":
                if (sender instanceof Player && !PlayerManager.hasPermission((Player) sender, Permissions.RELOAD)) {
                    MessageUtil.sendNoPermission(sender);
                    return true;
                }
                reload(sender);
                break;
            case "permissions":
            case "perms":
            case "p":
                permissions(sender);
                break;
            case "editor":
            case "creator":
            case "edit":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (PlayerManager.hasAnyPermission(player, Permissions.CREATOR_OPEN, Permissions.CREATOR_ALL)) {
                        FlyItemCreator.openMenu(player);
                    } else MessageUtil.sendNoPermission(player);
                } else MessageUtil.sendTranslation(sender, "error.player.not_player");;
                break;
            default:
                MessageUtil.sendTranslation(sender, "error.not_found.command");
                break;
        }
        return true;
    }

    private void help(CommandSender sender) {
        String timedFlyPre = MessageUtil.COMMAND_HELP_PREFIX + MessageUtil.COMMAND_TIMEDFLY;
        String tFlyPre = MessageUtil.COMMAND_HELP_PREFIX + MessageUtil.COMMAND_TFLY;

        MessageUtil.sendMessage(sender, MessageUtil.DIVIDER, false);
        MessageUtil.sendCenteredMessage(sender, MessageUtil.PLUGIN_NAME, MessageUtil.DIVIDER.length());

        List<String> commands = Arrays.stream(Arguments.TimedFly.values())
                .map(argument -> timedFlyPre + "&e" + argument.getUsage() + " &7- " + argument.getDescription())
                .collect(Collectors.toList());

        MessageUtil.sendMessages(sender, commands, false);
        MessageUtil.sendMessage(sender, tFlyPre + Arguments.TFly.HELP.getUsage() + " &7- " + Arguments.TFly.HELP.getDescription(), false);
        MessageUtil.sendMessage(sender, MessageUtil.DIVIDER, false);
    }

    private void permissions(CommandSender sender) {

        List<String> permissions = Arrays.stream(Permissions.values())
                .map(permission -> "&e" + permission.getPermission() + " &7- " + permission.getDescription())
                .collect(Collectors.toList());

        MessageUtil.sendMessage(sender, MessageUtil.DIVIDER, false);
        MessageUtil.sendCenteredMessage(sender, MessageUtil.PLUGIN_NAME + " Permissions", MessageUtil.DIVIDER.length());
        MessageUtil.sendMessages(sender, permissions, false);
        MessageUtil.sendMessage(sender, MessageUtil.DIVIDER, false);
    }

    private void reload(CommandSender sender) {
        try {
            for (Config config : Config.getConfigs().values()) {
                config.reload(false);
                if (config.getName().equals("items.yml")) {
                    config.get().getConfigurationSection("Items").getKeys(false).forEach(FlyItem::new);
                }
            }
        } catch (IOException e) {
            if (sender instanceof Player)
                MessageUtil.sendError((Player) sender, "Couldn't reload the plugin. Check the console...");
            MessageUtil.sendError(Bukkit.getConsoleSender(), e);
            return;
        }
        MessageUtil.sendMessage(sender, "Plugin successfully reloaded!");
    }
}
