package com.minestom.CMDs;

import com.minestom.ConfigurationFiles.LangFiles;
import com.minestom.Managers.CooldownManager;
import com.minestom.Managers.MessageManager;
import com.minestom.Managers.MySQLManager;
import com.minestom.Managers.TimeFormat;
import com.minestom.TimedFly;
import com.minestom.Utilities.BossBarManager;
import com.minestom.Utilities.GUI.FlyGUI;
import com.minestom.Utilities.GUI.GUIListener;
import com.minestom.Utilities.Others.GeneralListener;
import com.minestom.Utilities.Utility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class FlyCMD implements CommandExecutor {

    private TimedFly plugin = TimedFly.getInstance();
    private TimeFormat format = new TimeFormat();
    private LangFiles lang = LangFiles.getInstance();
    private Utility utility = new Utility(plugin);
    private BossBarManager bossBarManager = plugin.getBossBarManager();
    private MySQLManager sqlManager = new MySQLManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        FileConfiguration config = lang.getLang();
        FlyGUI gui = new FlyGUI();
        if (cmd.getName().equalsIgnoreCase("tfly")) {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    utility.message(sender, "§cOnly Player can use this command");
                    return true;
                }
                if (plugin.getConfig().getBoolean("UsePermission.Use") && !sender.hasPermission(plugin.getConfig().getString("UsePermission.Permission"))) {
                    utility.message(sender, MessageManager.NOPERM.toString());
                    Player player = (Player) sender;
                    plugin.getNMS().sendTitle(player, utility.color(config.getString("Other.NoPermission.Title")), 20, 40, 20);
                    plugin.getNMS().sendTitle(player, utility.color(config.getString("Other.NoPermission.SubTitle")), 20, 40, 20);
                    return true;
                }
                Player player = (Player) sender;
                if (CooldownManager.isInCooldown(player.getUniqueId(), "fly")) {
                    utility.message(player, config.getString("Other.OnCooldown").replace("%cooldown%",
                            format.format(CooldownManager.getTimeLeft(player.getUniqueId(), "fly"))));
                    return true;
                }
                if (utility.isWorldEnabled(player, player.getWorld())) {
                    gui.flyGui(player);
                    return true;
                } else {
                    utility.message(player, MessageManager.DISABLEDWORLD.toString());
                }
            }
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("help")) {
                    Bukkit.getServer().dispatchCommand(sender, "tf help");
                }
                if (args[0].equalsIgnoreCase("add")) {
                    if (sender.hasPermission("timedfly.admin") || sender.hasPermission("timedfly.fly.add")) {
                        try {
                            Player player = Bukkit.getPlayer(args[1]);
                            double time = Double.parseDouble(args[2]);
                            if (player == null) {
                                utility.message(sender, "&cTimedFly >> &aThe player &7" + args[1] + " &ais not online.");
                                return true;
                            }
                            if (utility.isWorldEnabled(player, player.getWorld())) {
                                if (!player.getAllowFlight()) {
                                    player.setAllowFlight(true);
                                }
                                if (!GUIListener.flytime.containsKey(player.getUniqueId())) {
                                    GeneralListener.initialTime.put(player.getUniqueId(), (int) time * 60);
                                    GUIListener.flytime.put(player.getUniqueId(), (int) time * 60);
                                    utility.message(sender, config.getString("Fly.Message.AddTime").replace("%time%", time + "").replace("%player%", player.getDisplayName()));
                                } else {
                                    GeneralListener.initialTime.put(player.getUniqueId(), GeneralListener.initialTime.get(player.getUniqueId()) + (int) (time * 60));
                                    GUIListener.flytime.put(player.getUniqueId(), GUIListener.flytime.get(player.getUniqueId()) + (int) time * 60);
                                    utility.message(sender, config.getString("Fly.Message.AddTime").replace("%time%", time + "").replace("%player%", player.getDisplayName()));
                                }
                                return true;
                            }
                            utility.message(sender, MessageManager.DISABLEDWORLD.toString());
                        } catch (Exception e) {
                            utility.message(sender, "&7Usage: /tfly add <player> <minutes>");
                        }
                    } else {
                        utility.message(sender, MessageManager.NOPERM.toString());
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            plugin.getNMS().sendTitle(player, utility.color(config.getString("Other.NoPermission.Title")), 20, 40, 20);
                            plugin.getNMS().sendTitle(player, utility.color(config.getString("Other.NoPermission.SubTitle")), 20, 40, 20);
                        }
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("set")) {
                    if (sender.hasPermission("timedfly.admin") || sender.hasPermission("timedfly.fly.set")) {
                        try {
                            Player player = Bukkit.getPlayer(args[1]);
                            double time = Double.parseDouble(args[2]);
                            if (player == null) {
                                utility.message(sender, "&cTimedFly >> &aThe player &7" + args[1] + " &ais not online.");
                                return true;
                            }
                            if (utility.isWorldEnabled(player, player.getWorld())) {
                                if (!player.getAllowFlight()) {
                                    player.setAllowFlight(true);
                                    player.setFlying(true);
                                }
                                GeneralListener.initialTime.put(player.getUniqueId(), (int) time * 60);
                                GUIListener.flytime.put(player.getUniqueId(), (int) time * 60);
                                if (plugin.getConfig().getBoolean("BossBarTimer.Enabled")) {
                                    bossBarManager.addPlayer(player);
                                }
                                utility.message(sender, config.getString("Fly.Message.ToPlayer")
                                        .replace("%target%", player.getDisplayName()).replace("%time%", "" + time));
                                utility.message(player, config.getString("Fly.Message.FromPlayer")
                                        .replace("%player%", sender.getName()).replace("%time%", "" + time));
                                return true;
                            } else utility.message(sender, MessageManager.DISABLEDWORLD.toString());
                        } catch (Exception e) {
                            utility.message(sender,
                                    "&7Usage: /tfly set <player> <minutes>");
                        }
                    } else {
                        utility.message(sender, MessageManager.NOPERM.toString());
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            plugin.getNMS().sendTitle(player, utility.color(config.getString("Other.NoPermission.Title")), 20, 40, 20);
                            plugin.getNMS().sendTitle(player, utility.color(config.getString("Other.NoPermission.SubTitle")), 20, 40, 20);
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
                        if (utility.isWorldEnabled(player, player.getWorld())) {
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
                        if (utility.isWorldEnabled(player, player.getWorld())) {
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
                    if (!(sender instanceof Player)) {
                        utility.message(sender, "§cOnly Player can use this command");
                        return true;
                    }
                    Player player = (Player) sender;
                    if (GUIListener.flytime.containsKey(player.getUniqueId())) {
                        if (utility.isWorldEnabled(player, player.getWorld())) {
                            utility.message(player, config.getString("Fly.Message.TimeLeft").replace("%timeleft%",
                                    format.format(GUIListener.flytime.get(player.getUniqueId()))));
                            return true;
                        } else utility.message(player, MessageManager.DISABLEDWORLD.toString());
                    } else {
                        if (utility.isWorldEnabled(player, player.getWorld())) {
                            utility.message(player, config.getString("Fly.Message.TimeLeft")
                                    .replace("%timeleft%", config.getString("Format.NoTimeLeft")));
                            return true;
                        } else utility.message(player, config.getString("Other.DisabledWorld"));
                    }
                }
                if (args[0].equalsIgnoreCase("stop")) {
                    if (!(sender instanceof Player)) {
                        utility.message(sender, "&cOnly players can do this");
                        return true;
                    }
                    Player player = (Player) sender;
                    if (!sender.hasPermission("timedfly.admin") || !sender.hasPermission("timedfly.fly.stopresume")) {
                        utility.message(sender, config.getString("Other.NoPermission.Message"));
                        plugin.getNMS().sendTitle(player, utility.color(config.getString("Other.NoPermission.Title")), 10, 40, 10);
                        plugin.getNMS().sendTitle(player, utility.color(config.getString("Other.NoPermission.SubTitle")), 10, 40, 10);
                        return true;
                    }
                    if (GUIListener.flytime.containsKey(player.getUniqueId())) {
                        if (plugin.getConfig().getBoolean("BossBarTimer.Enabled")) {
                            bossBarManager.removeBar(player);
                        }
                        sqlManager.setTimeLeft(player, GUIListener.flytime.get(player.getUniqueId()));
                        GUIListener.flytime.remove(player.getUniqueId());
                        utility.message(player, config.getString("Fly.Message.StopAndResume.Stop"));
                        if (player.getAllowFlight() || player.isFlying()) {
                            player.setAllowFlight(false);
                            player.setFlying(false);
                        }
                    } else utility.message(player, config.getString("Fly.Message.StopAndResume.NoTime"));
                }
                if (args[0].equalsIgnoreCase("resume")) {
                    if (!(sender instanceof Player)) {
                        utility.message(sender, "&cOnly players can do this");
                        return true;
                    }
                    Player player = (Player) sender;
                    if (!sender.hasPermission("timedfly.admin") || !sender.hasPermission("timedfly.fly.stopresume")) {
                        utility.message(sender, config.getString("Other.NoPermission.Message"));
                        plugin.getNMS().sendTitle(player, utility.color(config.getString("Other.NoPermission.Title")), 10, 40, 10);
                        plugin.getNMS().sendTitle(player, utility.color(config.getString("Other.NoPermission.SubTitle")), 10, 40, 10);
                        return true;
                    }
                    if (sqlManager.getTimeLeft(player) == 0) {
                        utility.message(player, config.getString("Fly.Message.StopAndResume.NoTime"));
                        return true;
                    }
                    if (GUIListener.flytime.containsKey(player.getUniqueId())) {
                        utility.message(player, config.getString("Fly.Message.StopAndResume.Already"));
                        return true;
                    }
                    GUIListener.flytime.put(player.getUniqueId(), sqlManager.getTimeLeft(player));
                    if (plugin.getConfig().getBoolean("BossBarTimer.Enabled")) {
                        bossBarManager.addPlayer(player);
                    }
                    utility.message(player, config.getString("Fly.Message.StopAndResume.Resume"));
                    if (!player.getAllowFlight()) {
                        player.setAllowFlight(true);
                        player.setFlying(true);
                    }
                }
            }
        }
        return true;
    }

}
