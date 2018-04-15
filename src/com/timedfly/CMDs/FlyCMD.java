package com.timedfly.CMDs;

import com.timedfly.ConfigurationFiles.ConfigCache;
import com.timedfly.ConfigurationFiles.LangFiles;
import com.timedfly.Listeners.GUIListener;
import com.timedfly.Managers.MessageManager;
import com.timedfly.Managers.TimeFormat;
import com.timedfly.TimedFly;
import com.timedfly.Utilities.FlyGUI;
import com.timedfly.Utilities.PlayerCache;
import com.timedfly.Utilities.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class FlyCMD implements CommandExecutor {

    private TimedFly plugin;
    private LangFiles lang = LangFiles.getInstance();
    private Utility utility;
    private ConfigCache configCache;

    public FlyCMD(TimedFly plugin, Utility utility, ConfigCache configCache) {
        this.plugin = plugin;
        this.utility = utility;
        this.configCache = configCache;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        FileConfiguration config = lang.getLang();
        FlyGUI gui = new FlyGUI(utility, configCache);

        if (cmd.getName().equalsIgnoreCase("tfly")) {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    utility.message(sender, "§cOnly Player can use this command");
                    return true;
                }
                if (configCache.isUsePermission() && !sender.hasPermission(configCache.getPermission())) {
                    utility.message(sender, MessageManager.NOPERM.toString());
                    Player player = (Player) sender;
                    plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.Title")), 20, 40, 20);
                    plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.SubTitle")), 20, 40, 20);
                    return true;
                }
                Player player = (Player) sender;

                if (utility.isWorldEnabled(player.getWorld())) {
                    gui.flyGui(player);
                    return true;
                } else {
                    utility.message(player, MessageManager.DISABLEDWORLD.toString());
                }
            } else {
                if (args[0].equalsIgnoreCase("help")) {
                    Bukkit.getServer().dispatchCommand(sender, "tf help");
                }
                if (args[0].equalsIgnoreCase("add")) {
                    if (sender.hasPermission("timedfly.admin") || sender.hasPermission("timedfly.fly.add")) {
                        if (args.length == 1 || args.length == 2) {
                            utility.message(sender, "&7Usage: /tfly add <player> <minutes>");
                            return true;
                        }
                        Player player = Bukkit.getPlayer(args[1]);
                        PlayerCache playerCache = utility.getPlayerCache(player);

                        StringBuilder timeString = new StringBuilder();
                        for (int i = 2; i < args.length; i++)
                            timeString.append(args[i]).append(" ");
                        long time = utility.timeToSeconds(timeString.toString().trim());

                        if (player == null) {
                            utility.message(sender, "&cTimedFly >> &aThe player &7" + args[1] + " &ais not online.");
                            return true;
                        }
                        if (utility.isWorldEnabled(player.getWorld())) {
                            if (playerCache.isTimeStopped()) {
                                playerCache.setTimeLeft(playerCache.getTimeLeft() + (int) time);
                                utility.message(sender, config.getString("Fly.Message.AddTime").replace("%time%", timeString.toString().trim()).replace("%player%", player.getDisplayName()));
                                return true;
                            }
                            if (!GUIListener.flytime.containsKey(player.getUniqueId())) {
                                playerCache.setInitialTime((int) time);
                                playerCache.setTimeLeft((int) time);

                                GUIListener.flytime.put(player.getUniqueId(), (int) time);
                            } else {
                                playerCache.setInitialTime((int) time + playerCache.getInitialTime());
                                playerCache.setTimeLeft((int) time + playerCache.getTimeLeft());

                                GUIListener.flytime.put(player.getUniqueId(), GUIListener.flytime.get(player.getUniqueId()) + (int) time);
                            }

                            playerCache.setFlying(true);

                            if (configCache.isBossBarTimerEnabled())
                                playerCache.getBossBarManager().setInitialTime(playerCache.getInitialTime())
                                        .setCurrentTime(playerCache.getTimeLeft()).setBarProgress(playerCache.getTimeLeft()).show();

                            utility.message(sender, config.getString("Fly.Message.AddTime").replace("%time%", timeString.toString().trim()).replace("%player%", player.getDisplayName()));
                            utility.message(player, config.getString("Fly.Message.AddTimeToPlayer").replace("%time%", timeString.toString().trim()).replace("%player%", sender.getName()));
                            return true;
                        }
                        utility.message(sender, MessageManager.DISABLEDWORLD.toString());

                    } else {
                        utility.message(sender, MessageManager.NOPERM.toString());
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.Title")), 20, 40, 20);
                            plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.SubTitle")), 20, 40, 20);
                        }
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("set")) {
                    if (sender.hasPermission("timedfly.admin") || sender.hasPermission("timedfly.fly.set")) {
                        if (args.length == 1 || args.length == 2) {
                            utility.message(sender, "&7Usage: /tfly set <player> <minutes>");
                            return true;
                        }
                        Player player = Bukkit.getPlayer(args[1]);
                        PlayerCache playerCache = utility.getPlayerCache(player);

                        StringBuilder timeString = new StringBuilder();
                        for (int i = 2; i < args.length; i++)
                            timeString.append(args[i]).append(" ");
                        long time = utility.timeToSeconds(timeString.toString().trim());

                        if (player == null) {
                            utility.message(sender, "&cTimedFly >> &aThe player &7" + args[1] + " &ais not online.");
                            return true;
                        }
                        if (utility.isWorldEnabled(player.getWorld())) {
                            if (playerCache.isTimeStopped()) {
                                playerCache.setTimeLeft((int) time);
                                utility.message(sender, config.getString("Fly.Message.ToPlayer")
                                        .replace("%target%", player.getDisplayName()).replace("%time%", "" + timeString.toString().trim()));
                                utility.message(player, config.getString("Fly.Message.FromPlayer")
                                        .replace("%player%", sender.getName()).replace("%time%", "" + timeString.toString().trim()));
                                return true;
                            }

                            playerCache.setFlying(true);
                            playerCache.setInitialTime((int) time);
                            playerCache.setTimeLeft((int) time);
                            GUIListener.flytime.put(player.getUniqueId(), (int) time);

                            if (configCache.isBossBarTimerEnabled())
                                playerCache.getBossBarManager().setInitialTime((int) time).setCurrentTime((int) time).setBarProgress((int) time).show();

                            utility.message(sender, config.getString("Fly.Message.ToPlayer")
                                    .replace("%target%", player.getDisplayName()).replace("%time%", "" + timeString.toString().trim()));
                            utility.message(player, config.getString("Fly.Message.FromPlayer")
                                    .replace("%player%", sender.getName()).replace("%time%", "" + timeString.toString().trim()));
                            return true;
                        } else utility.message(sender, MessageManager.DISABLEDWORLD.toString());

                    } else {
                        utility.message(sender, MessageManager.NOPERM.toString());
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.Title")), 20, 40, 20);
                            plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.SubTitle")), 20, 40, 20);
                        }
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("on")) {
                    if (!(sender instanceof Player)) {
                        utility.message(sender, "§cOnly Player can use this command");
                        return true;
                    }
                    Player player = (Player) sender;
                    if (player.hasPermission("timedfly.admin") || player.hasPermission("timedfly.fly.onoff")) {
                        if (utility.isWorldEnabled(player.getWorld())) {
                            player.setAllowFlight(true);
                            utility.message(player, config.getString("Fly.Message.SetOn"));
                            return true;
                        } else utility.message(player, MessageManager.DISABLEDWORLD.toString());
                    } else {
                        utility.message(player, MessageManager.NOPERM.toString());
                        return true;
                    }

                }
                if (args[0].equalsIgnoreCase("off")) {
                    if (!(sender instanceof Player)) {
                        utility.message(sender, "§cOnly Player can use this command");
                        return true;
                    }
                    Player player = (Player) sender;
                    if (player.hasPermission("timedfly.admin") || player.hasPermission("timedfly.fly.onoff")) {
                        if (utility.isWorldEnabled(player.getWorld())) {
                            player.setAllowFlight(false);
                            utility.message(player, config.getString("Fly.Message.SetOff"));
                            return true;
                        } else utility.message(player, MessageManager.DISABLEDWORLD.toString());
                    } else {
                        utility.message(sender, MessageManager.NOPERM.toString());

                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("timeleft")) {
                    if (args.length == 1) {
                        if (!(sender instanceof Player)) {
                            utility.message(sender, "§7Usage &a/tfly timeleft <player>");
                            return true;
                        }
                        Player player = (Player) sender;
                        if (GUIListener.flytime.containsKey(player.getUniqueId())) {
                            long timeLeft = GUIListener.flytime.get(player.getUniqueId());
                            utility.message(player, config.getString("Fly.Message.TimeLeft")
                                    .replace("%timeleft%", TimeFormat.format(timeLeft)));
                        } else {
                            PlayerCache playerCache = utility.getPlayerCache(player);
                            utility.message(player, config.getString("Fly.Message.TimeLeft")
                                    .replace("%timeleft%", playerCache.getTimeLeft() != 0 && playerCache.isTimeStopped() || playerCache.isTimeOnHold()
                                            ? TimeFormat.format(playerCache.getTimeLeft()) : config.getString("Format.NoTimeLeft")));
                            return true;
                        }
                    } else if (args.length == 2) {
                        Player target = Bukkit.getPlayer(args[1]);
                        if (target == null) {
                            utility.message(sender, config.getString("Other.PlayerNotFound"));
                            return true;
                        }
                        if (GUIListener.flytime.containsKey(target.getUniqueId())) {
                            long timeLeft = GUIListener.flytime.get(target.getUniqueId());
                            utility.message(sender, config.getString("Fly.Message.OthersTimeLeft")
                                    .replace("%timeleft%", TimeFormat.format(timeLeft)).replace("%player%", target.getName()));
                        } else {
                            PlayerCache playerCache = utility.getPlayerCache(target);
                            utility.message(sender, config.getString("Fly.Message.TimeLeft")
                                    .replace("%timeleft%", playerCache.getTimeLeft() != 0 && playerCache.isTimeStopped() || playerCache.isTimeOnHold()
                                            ? TimeFormat.format(playerCache.getTimeLeft()) : config.getString("Format.NoTimeLeft")));
                            return true;
                        }
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("stop")) {
                    if (!(sender instanceof Player)) {
                        utility.message(sender, "&cOnly players can do this");
                        return true;
                    }
                    Player player = (Player) sender;
                    PlayerCache playerCache = utility.getPlayerCache(player);

                    if (!utility.isWorldEnabled(player.getWorld())) {
                        utility.message(player, config.getString("Other.DisabledWorld"));
                        return true;
                    }
                    if (player.hasPermission("timedfly.admin") || player.hasPermission("timedfly.fly.stopresume")) {
                        if (GUIListener.flytime.containsKey(player.getUniqueId())) {

                            if (configCache.isBossBarTimerEnabled()) playerCache.getBossBarManager().hide();

                            playerCache.setTimeLeft(GUIListener.flytime.get(player.getUniqueId()));
                            playerCache.setTimeStopped(true);
                            playerCache.setFlying(false);

                            GUIListener.flytime.remove(player.getUniqueId());
                            GUIListener.after.add(player.getUniqueId());
                            Bukkit.getScheduler().runTaskLater(plugin, () -> GUIListener.after.remove(player.getUniqueId()), 6 * 20L);

                            utility.message(player, config.getString("Fly.Message.StopAndResume.Stop"));
                        } else utility.message(player, config.getString("Fly.Message.StopAndResume.NoTime"));
                    } else {
                        utility.message(sender, config.getString("Other.NoPermission.Message"));
                        plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.Title")), 10, 40, 10);
                        plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.SubTitle")), 10, 40, 10);
                        return true;
                    }
                }

                if (args[0].equalsIgnoreCase("resume")) {
                    if (!(sender instanceof Player)) {
                        utility.message(sender, "&cOnly players can do this");
                        return true;
                    }
                    Player player = (Player) sender;
                    PlayerCache playerCache = utility.getPlayerCache(player);

                    if (!utility.isWorldEnabled(player.getWorld())) {
                        utility.message(player, config.getString("Other.DisabledWorld"));
                        return true;
                    }
                    if (player.hasPermission("timedfly.admin") || player.hasPermission("timedfly.fly.stopresume")) {
                        if (playerCache.getTimeLeft() == 0) {
                            utility.message(player, config.getString("Fly.Message.StopAndResume.NoTime"));
                            return true;
                        }
                        if (GUIListener.flytime.containsKey(player.getUniqueId())) {
                            utility.message(player, config.getString("Fly.Message.StopAndResume.Already"));
                            return true;
                        }
                        GUIListener.flytime.put(player.getUniqueId(), playerCache.getTimeLeft());
                        playerCache.setTimeStopped(false);
                        playerCache.setFlying(true);

                        if (configCache.isBossBarTimerEnabled()) playerCache.getBossBarManager().show();
                        utility.message(player, config.getString("Fly.Message.StopAndResume.Resume"));
                    } else {
                        utility.message(sender, config.getString("Other.NoPermission.Message"));
                        plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.Title")), 10, 40, 10);
                        plugin.getNMS().sendTitle(player, Utility.color(config.getString("Other.NoPermission.SubTitle")), 10, 40, 10);
                        return true;
                    }
                }
            }
        }
        return true;
    }
}

