package com.minestom.CMDs;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.minestom.TimedFly;
import com.minestom.Languages.LangFiles;
import com.minestom.Utilities.GUI.FlyGUI;
import com.minestom.Utilities.GUI.GUIListener;

import be.maximvdw.titlemotd.ui.Title;

public class FlyCMD implements CommandExecutor {

	public static TimedFly plugin = TimedFly.getPlugin(TimedFly.class);
	private LangFiles lang = LangFiles.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
		FileConfiguration config = lang.getLang();
		if (cmd.getName().equalsIgnoreCase("fly")) {
			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("¡ìcOnly Player can use this command");
					return true;
				}
				Player player = (Player) sender;
				List<String> worlds = plugin.getConfig().getStringList("Enabled-Worlds");
				for (String world : worlds) {
					World w = Bukkit.getWorld(world);
					if (player.getWorld() == w) {
						FlyGUI.flyGui(player);
						return true;
					}
				}
				player.sendMessage(
						ChatColor.translateAlternateColorCodes('&', config.getString("Other.DisabledWorld")));
			}
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("set")) {
					if (sender.hasPermission("timedfly.admin") || sender.hasPermission("timedfly.fly.set")) {
						try {
							Player player = Bukkit.getPlayer(args[1]);
							int time = Integer.parseInt(args[2]);
							List<String> worlds = plugin.getConfig().getStringList("Enabled-Worlds");
							if (player == null) {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
										"&cTimedFly >> &aThe player &7" + args[1] + " &ais not online."));
								return true;
							}
							for (String world : worlds) {
								World w = Bukkit.getWorld(world);
								if (player.getWorld() == w) {
									if (player.getAllowFlight() == false) {
										player.setAllowFlight(true);
										player.setFlying(true);
									}
									GUIListener.cooldown.put(player.getUniqueId(), time * 60);
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
											config.getString("Fly.Message.ToPlayer")
													.replace("%target%", player.getName())
													.replace("%time%", "" + time)));
									player.sendMessage(ChatColor.translateAlternateColorCodes('&',
											config.getString("Fly.Message.FromPlayer")
													.replace("%player%", sender.getName())
													.replace("%time%", "" + time)));
									return true;
								}
							}
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
									config.getString("Other.DisabledWorld")));
						} catch (Exception e) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
									"&cTimedFly >> &7Usage: /fly set <player> <minutes>"));
						}
					} else {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								config.getString("Other.NoPermission.Message")));
						if (sender instanceof Player) {
							Player player = (Player) sender;
							if (Bukkit.getVersion().contains("1.8")) {
								Title title = new Title(
										ChatColor.translateAlternateColorCodes('&',
												config.getString("Other.NoPermission.Title")),
										ChatColor.translateAlternateColorCodes('&',
												config.getString("Other.NoPermission.SubTitle")),
										1, 2, 1);
								title.setTimingsToSeconds();
								title.send(player);
							} else {
								player.sendTitle(
										ChatColor.translateAlternateColorCodes('&',
												config.getString("Other.NoPermission.Title")),
										ChatColor.translateAlternateColorCodes('&',
												config.getString("Other.NoPermission.SubTitle")),
										1 * 20, 2 * 20, 1 * 20);
							}
						}
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("on")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("¡ìcOnly Player can use this command");
						return true;
					}
					Player player = (Player) sender;
					List<String> worlds = plugin.getConfig().getStringList("Enabled-Worlds");
					if (player.hasPermission("timedfly.admin") || player.hasPermission("timedfly.fly.onoff")) {
						for (String world : worlds) {
							World w = Bukkit.getWorld(world);
							if (player.getWorld() == w) {
								player.setAllowFlight(true);
								player.sendMessage(ChatColor.translateAlternateColorCodes('&',
										config.getString("Fly.Message.SetOn")));
								return true;
							}
						}
						player.sendMessage(
								ChatColor.translateAlternateColorCodes('&', config.getString("Other.DisabledWorld")));
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&',
								config.getString("Other.NoPermission.Message")));
						if (Bukkit.getVersion().contains("1.8")) {
							Title title = new Title(
									ChatColor.translateAlternateColorCodes('&',
											config.getString("Other.NoPermission.Title")),
									ChatColor.translateAlternateColorCodes('&',
											config.getString("Other.NoPermission.SubTitle")),
									1, 2, 1);
							title.setTimingsToSeconds();
							title.send(player);
						} else {
							player.sendTitle(
									ChatColor.translateAlternateColorCodes('&',
											config.getString("Other.NoPermission.Title")),
									ChatColor.translateAlternateColorCodes('&',
											config.getString("Other.NoPermission.SubTitle")),
									1 * 20, 2 * 20, 1 * 20);
						}
						return true;
					}

				}
				if (args[0].equalsIgnoreCase("off")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("¡ìcOnly Player can use this command");
						return true;
					}
					Player player = (Player) sender;
					List<String> worlds = plugin.getConfig().getStringList("Enabled-Worlds");
					if (player.hasPermission("timedfly.admin") || player.hasPermission("timedfly.fly.onoff")) {
						for (String world : worlds) {
							World w = Bukkit.getWorld(world);
							if (player.getWorld() == w) {
								player.setAllowFlight(false);
								player.sendMessage(ChatColor.translateAlternateColorCodes('&',
										config.getString("Fly.Message.SetOff")));
								return true;
							}
						}
						player.sendMessage(
								ChatColor.translateAlternateColorCodes('&', config.getString("Other.DisabledWorld")));
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&',
								config.getString("Other.NoPermission.Message")));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								config.getString("Other.NoPermission.Message")));
						if (Bukkit.getVersion().contains("1.8")) {
							Title title = new Title(
									ChatColor.translateAlternateColorCodes('&',
											config.getString("Other.NoPermission.Title")),
									ChatColor.translateAlternateColorCodes('&',
											config.getString("Other.NoPermission.SubTitle")),
									1, 2, 1);
							title.setTimingsToSeconds();
							title.send(player);
						} else {
							player.sendTitle(
									ChatColor.translateAlternateColorCodes('&',
											config.getString("Other.NoPermission.Title")),
									ChatColor.translateAlternateColorCodes('&',
											config.getString("Other.NoPermission.SubTitle")),
									1 * 20, 2 * 20, 1 * 20);
						}
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("timeleft")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage("¡ìcOnly Player can use this command");
						return true;
					}
					Player player = (Player) sender;
					List<String> worlds = plugin.getConfig().getStringList("Enabled-Worlds");
					if (GUIListener.cooldown.containsKey(player.getUniqueId())) {
						Integer millis = GUIListener.cooldown.get(player.getUniqueId()) * 1000;
						TimeZone tz = TimeZone.getTimeZone("UTC");
						SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
						df.setTimeZone(tz);
						String timel = df.format(new Date(millis));
						for (String world : worlds) {
							World w = Bukkit.getWorld(world);
							if (player.getWorld() == w) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&',
										config.getString("Fly.Message.TimeLeft").replace("%timeleft%", timel)));
								return true;
							}
						}
						player.sendMessage(
								ChatColor.translateAlternateColorCodes('&', config.getString("Other.DisabledWorld")));
					} else {
						for (String world : worlds) {
							World w = Bukkit.getWorld(world);
							if (player.getWorld() == w) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&',
										config.getString("Fly.Message.TimeLeft").replace("%timeleft%", "00:00:00")));
								return true;
							}
						}
						player.sendMessage(
								ChatColor.translateAlternateColorCodes('&', config.getString("Other.DisabledWorld")));
					}
				}
			}
		}
		return true;
	}

}
