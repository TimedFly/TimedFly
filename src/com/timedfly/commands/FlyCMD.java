package com.timedfly.commands;

import com.timedfly.NMS.NMS;
import com.timedfly.configurations.ConfigCache;
import com.timedfly.configurations.Languages;
import com.timedfly.managers.PlayerManager;
import com.timedfly.utilities.FlyGUI;
import com.timedfly.utilities.Message;
import com.timedfly.utilities.TimeFormat;
import com.timedfly.utilities.Utilities;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class FlyCMD implements CommandExecutor {

    private Languages languages;
    private Utilities utility;
    private FlyGUI flyGUI;
    private NMS nms;

    public FlyCMD(Languages languages, Utilities utility, FlyGUI flyGUI, NMS nms) {
        this.languages = languages;
        this.utility = utility;
        this.flyGUI = flyGUI;
        this.nms = nms;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        FileConfiguration languageConfig = languages.getLanguageFile();

        if (cmd.getName().equalsIgnoreCase("tfly")) {
            if (args.length == 0) {
                if (!ConfigCache.isGuiEnable()) return true;
                if (!(sender instanceof Player)) {
                    Message.sendMessage(sender, "§cOnly Player can use this command");
                    return true;
                }
                Player player = (Player) sender;
                if (ConfigCache.isUsePermission() && !sender.hasPermission(ConfigCache.getPermission())) {
                    Message.sendNoPermission(player, languageConfig, nms);
                    return true;
                }

                if (!utility.isWorldEnabled(player.getWorld())) {
                    Message.sendDisabledWorld(sender, languageConfig);
                    return true;
                }
                flyGUI.openGui(player);

            } else {
                if (args[0].equalsIgnoreCase("help")) Bukkit.getServer().dispatchCommand(sender, "tf help");
                if (args[0].equalsIgnoreCase("help2")) Bukkit.getServer().dispatchCommand(sender, "tf help2");
                if (args[0].equalsIgnoreCase("help3")) Bukkit.getServer().dispatchCommand(sender, "tf help3");
                if (args[0].equalsIgnoreCase("add")) {
                    if (!(sender.hasPermission("timedfly.fly.add") || sender.hasPermission("timedfly.admin"))) {
                        if (sender instanceof Player) Message.sendNoPermission((Player) sender, languageConfig, nms);
                        return true;
                    }
                    if (args.length == 1 || args.length == 2) {
                        Message.sendMessage(sender, "&7Usage: /tfly add <player> <minutes>");
                        return true;
                    }

                    StringBuilder timeString = new StringBuilder();

                    if (StringUtils.isNumeric(args[2])) timeString.append(args[2]);
                    else for (int i = 2; i < args.length; i++) timeString.append(args[i]).append(" ");

                    long time = TimeFormat.timeToSeconds(timeString.toString().trim());

                    if (args[1].equalsIgnoreCase("*")) {
                        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:AddCmd: &7Adding time to all player", 1);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (!utility.isWorldEnabled(player.getWorld())) continue;
                            PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());
                            playerManager.addTime((int) time);
                        }
                        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:AddCmd: &7Success", 2);
                        return true;
                    }

                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        Message.sendMessage(sender, languageConfig.getString("Other.PlayerNotFound"));
                        return true;
                    }

                    if (!utility.isWorldEnabled(player.getWorld())) {
                        Message.sendDisabledWorld(sender, languageConfig);
                        return true;
                    }

                    PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());

                    playerManager.addTime((int) time);

                    Message.sendMessage(sender, languageConfig.getString("Fly.Message.AddTime").replace("%time%",
                            timeString.toString().trim()).replace("%player%", player.getDisplayName()));
                    Message.sendMessage(player, languageConfig.getString("Fly.Message.AddTimeToPlayer").replace("%time%",
                            timeString.toString().trim()).replace("%player%", sender.getName()));
                    return true;
                }
                if (args[0].equalsIgnoreCase("set")) {
                    if (!(sender.hasPermission("timedfly.fly.set") || sender.hasPermission("timedfly.admin"))) {
                        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:SetCmd: &7No permission", 1);
                        if (sender instanceof Player) Message.sendNoPermission((Player) sender, languageConfig, nms);
                        return true;
                    }
                    if (args.length == 1 || args.length == 2) {
                        Message.sendMessage(sender, "&7Usage: /tfly set <player> <minutes>");
                        return true;
                    }

                    StringBuilder timeString = new StringBuilder();

                    if (StringUtils.isNumeric(args[2])) timeString.append(args[2]);
                    else for (int i = 2; i < args.length; i++) timeString.append(args[i]).append(" ");

                    long time = TimeFormat.timeToSeconds(timeString.toString().trim());

                    if (args[1].equalsIgnoreCase("*")) {
                        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:SetCmd: &7Setting time to all player", 2);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (!utility.isWorldEnabled(player.getWorld())) continue;
                            PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());
                            playerManager.setTime((int) time);
                        }
                        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:SetCmd: &7Success", 2);
                        return true;
                    }

                    Player player = Bukkit.getPlayer(args[1]);

                    if (player == null) {
                        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:SetCmd: &7Player not found", 1);
                        Message.sendMessage(sender, languageConfig.getString("Other.PlayerNotFound"));
                        return true;
                    }

                    if (!utility.isWorldEnabled(player.getWorld())) {
                        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:SetCmd: &7Disabled world", 1);
                        Message.sendDisabledWorld(sender, languageConfig);
                        return true;
                    }

                    PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());

                    Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:SetCmd: Setting Time", 2);
                    playerManager.setTime((int) time);

                    Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:SetCmd: &7Sending success message", 2);
                    Message.sendMessage(sender, languageConfig.getString("Fly.Message.ToPlayer")
                            .replace("%target%", player.getDisplayName()).replace("%time%", "" + timeString.toString().trim()));
                    Message.sendMessage(player, languageConfig.getString("Fly.Message.FromPlayer")
                            .replace("%player%", sender.getName()).replace("%time%", "" + timeString.toString().trim()));
                    return true;
                }
                if (args[0].equalsIgnoreCase("on")) {
                    if (!(sender instanceof Player)) {
                        Message.sendMessage(sender, "§cOnly Player can use this command");
                        return true;
                    }
                    Player player = (Player) sender;

                    if (!(player.hasPermission("timedfly.fly.onoff") || player.hasPermission("timedfly.admin"))) {
                        Message.sendNoPermission(player, languageConfig, nms);
                        return true;
                    }

                    if (!utility.isWorldEnabled(player.getWorld())) {
                        Message.sendDisabledWorld(sender, languageConfig);
                        return true;
                    }

                    player.setAllowFlight(true);
                    Message.sendMessage(player, languageConfig.getString("Fly.Message.SetOn"));
                    return true;
                }
                if (args[0].equalsIgnoreCase("off")) {
                    if (!(sender instanceof Player)) {
                        Message.sendMessage(sender, "§cOnly Player can use this command");
                        return true;
                    }
                    Player player = (Player) sender;

                    if (!(player.hasPermission("timedfly.fly.onoff") || player.hasPermission("timedfly.admin"))) {
                        Message.sendNoPermission(player, languageConfig, nms);
                        return true;
                    }

                    if (!utility.isWorldEnabled(player.getWorld())) {
                        Message.sendDisabledWorld(sender, languageConfig);
                        return true;
                    }

                    player.setAllowFlight(false);
                    Message.sendMessage(player, languageConfig.getString("Fly.Message.SetOn"));
                    return true;
                }
                if (args[0].equalsIgnoreCase("timeleft")) {
                    Player player;

                    if (args.length == 1) player = (Player) sender;
                    else player = Bukkit.getPlayer(args[1]);

                    if (!(sender instanceof Player)) {
                        Message.sendMessage(sender, "§7Usage &a/tfly timeleft <player>");
                        return true;
                    }

                    PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());

                    if (playerManager.getTimeLeft() > 0) {
                        long timeLeft = playerManager.getTimeLeft();
                        Message.sendMessage(player, languageConfig.getString("Fly.Message.TimeLeft")
                                .replace("%timeleft%", TimeFormat.formatLong(timeLeft)));
                    } else {
                        Message.sendMessage(player, languageConfig.getString("Fly.Message.TimeLeft")
                                .replace("%timeleft%", playerManager.getTimeLeft() != 0 || playerManager.isTimePaused()
                                        ? TimeFormat.formatLong(playerManager.getTimeLeft()) : languageConfig.getString("Format.NoTimeLeft")));
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("stop")) {
                    if (!(sender instanceof Player)) {
                        Message.sendMessage(sender, "&cOnly players can do this");
                        return true;
                    }

                    Player player = (Player) sender;
                    PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());

                    if (!(player.hasPermission("timedfly.fly.stopresume") || player.hasPermission("timedfly.admin"))) {
                        Message.sendNoPermission(player, languageConfig, nms);
                        return true;
                    }

                    if (!utility.isWorldEnabled(player.getWorld())) {
                        Message.sendDisabledWorld(sender, languageConfig);
                        return true;
                    }

                    if (playerManager.isTimeEnded()) {
                        Message.sendMessage(player, languageConfig.getString("Fly.Message.StopAndResume.NoTime"));
                        return true;
                    }

                    if (ConfigCache.isBossBarTimerEnabled()) playerManager.getBossBarManager().hide();

                    playerManager.stopTimedFly(true, true);
                    playerManager.setTimePaused(true);

                    Message.sendMessage(player, languageConfig.getString("Fly.Message.StopAndResume.Stop"));

                }

                if (args[0].equalsIgnoreCase("resume")) {
                    if (!(sender instanceof Player)) {
                        Message.sendMessage(sender, "&cOnly players can do this");
                        return true;
                    }
                    Player player = (Player) sender;
                    PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());

                    if (!(player.hasPermission("timedfly.fly.stopresume") || player.hasPermission("timedfly.admin"))) {
                        Message.sendNoPermission(player, languageConfig, nms);
                        return true;
                    }

                    if (!utility.isWorldEnabled(player.getWorld())) {
                        Message.sendMessage(player, languageConfig.getString("Other.DisabledWorld"));
                        return true;
                    }

                    if (playerManager.getTimeLeft() == 0) {
                        Message.sendMessage(player, languageConfig.getString("Fly.Message.StopAndResume.NoTime"));
                        return true;
                    }

                    if (!playerManager.isTimePaused()) {
                        Message.sendMessage(player, languageConfig.getString("Fly.Message.StopAndResume.Already"));
                        return true;
                    }
                    playerManager.startTimedFly();

                    if (ConfigCache.isBossBarTimerEnabled()) playerManager.getBossBarManager().show();
                    Message.sendMessage(player, languageConfig.getString("Fly.Message.StopAndResume.Resume"));

                }
            }
        }
        return true;
    }
}

