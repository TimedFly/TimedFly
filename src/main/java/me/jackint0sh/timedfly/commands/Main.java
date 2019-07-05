package me.jackint0sh.timedfly.commands;

import me.jackint0sh.timedfly.flygui.FlyItem;
import me.jackint0sh.timedfly.flygui.FlyItemCreator;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.utilities.Permissions;
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
                reload(sender);
                break;
            case "permissions":
            case "perms":
            case "p":
                permissions(sender);
                break;
            case "editor":
                if (sender instanceof Player) FlyItemCreator.openMenu((Player) sender);
                else MessageUtil.sendError("Only players allowed!");
                break;
            default:
                MessageUtil.sendMessage(sender, "Command not found. Try using " + MessageUtil.COMMAND_TIMEDFLY + "help");
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
                if (config.getName().equals("items.yml"))
                    config.get().getConfigurationSection("Items").getKeys(false).forEach(FlyItem::new);
                config.reload();
            }
        } catch (IOException e) {
            if (sender instanceof Player)
                MessageUtil.sendError((Player) sender, "Couldn't reload the plugin. Check the console...");
            else MessageUtil.sendError(e.getMessage());
            e.printStackTrace();
        }
        MessageUtil.sendMessage(sender, "Plugin successfully reloaded!");
    }
}
