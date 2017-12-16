package com.minestom.CMDs;

import com.minestom.ConfigurationFiles.LangFiles;
import com.minestom.Managers.CooldownManager;
import com.minestom.Managers.MessageManager;
import com.minestom.Managers.TimeFormat;
import com.minestom.TimedFly;
import com.minestom.Utilities.GUI.FlyGUI;
import com.minestom.Utilities.GUI.GUIListener;
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
                if (utility.isWorldEnabled(player)) {
                    gui.flyGui(player);
                    return true;
                } else {
                    utility.message(player, MessageManager.DISABLEDWORLD.toString());
                }
            }
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("add")) {
                    if (sender.hasPermission("timedfly.admin") || sender.hasPermission("timedfly.fly.add")) {
                        try {
                            Player player = Bukkit.getPlayer(args[1]);
                            int time = Integer.parseInt(args[2]);
                            if (player == null) {
                                utility.message(sender, "&cTimedFly >> &aThe player &7" + args[1] + " &ais not online.");
                                return true;
                            }
                            if (utility.isWorldEnabled(player)) {
                                if (!player.getAllowFlight()) {
                                    player.setAllowFlight(true);
                                }
                                if (!GUIListener.flytime.containsKey(player.getUniqueId())) {
                                    GUIListener.flytime.put(player.getUniqueId(), time * 60);
                                } else {
                                    GUIListener.flytime.put(player.getUniqueId(), GUIListener.flytime.get(player.getUniqueId()) + time * 60);
                                }
                                return true;
                            }
                            utility.message(sender, MessageManager.DISABLEDWORLD.toString());
                        } catch (Exception e) {
                            utility.message(sender, "&7Usage: fly add <player> <minutes>");
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
                            int time = Integer.parseInt(args[2]);
                            if (player == null) {
                                utility.message(sender, "&cTimedFly >> &aThe player &7" + args[1] + " &ais not online.");
                                return true;
                            }
                            if (utility.isWorldEnabled(player)) {
                                if (!player.getAllowFlight()) {
                                    player.setAllowFlight(true);
                                    player.setFlying(true);
                                }
                                GUIListener.flytime.put(player.getUniqueId(), time * 60);
                                utility.message(sender, config.getString("Fly.Message.ToPlayer")
                                        .replace("%target%", player.getName()).replace("%time%", "" + time));
                                utility.message(player, config.getString("Fly.Message.FromPlayer")
                                        .replace("%player%", sender.getName()).replace("%time%", "" + time));
                                return true;
                            }
                            utility.message(sender, MessageManager.DISABLEDWORLD.toString());
                        } catch (Exception e) {
                            utility.message(sender,
                                    "&7Usage: fly set <player> <minutes>");
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
                        if (utility.isWorldEnabled(player)) {
                            player.setAllowFlight(true);
                            utility.message(player, config.getString("Fly.Message.SetOn"));
                            return true;
                        }
                        utility.message(player, MessageManager.DISABLEDWORLD.toString());
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
                        if (utility.isWorldEnabled(player)) {
                            player.setAllowFlight(false);
                            utility.message(player, config.getString("Fly.Message.SetOff"));
                            return true;
                        }
                        utility.message(player, MessageManager.DISABLEDWORLD.toString());
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
                        if (utility.isWorldEnabled(player)) {
                            utility.message(player, config.getString("Fly.Message.TimeLeft").replace("%timeleft%",
                                    format.format(GUIListener.flytime.get(player.getUniqueId()))));
                            return true;
                        }
                        utility.message(player, MessageManager.DISABLEDWORLD.toString());
                    } else {
                        if (utility.isWorldEnabled(player)) {
                            utility.message(player, config.getString("Fly.Message.TimeLeft")
                                    .replace("%timeleft%", config.getString("Format.NoTimeLeft")));
                            return true;
                        }
                        utility.message(player, config.getString("Other.DisabledWorld"));
                    }
                }
            }
        }
        return true;
    }

}
