package com.minestom.CMDs;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.minestom.TimedFly;
import com.minestom.Utilities.FlyGUI;
import com.minestom.Utilities.GUIListener;

import be.maximvdw.titlemotd.ui.Title;

public class FlyCMD implements CommandExecutor {

	public static TimedFly plugin = TimedFly.getPlugin(TimedFly.class);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
		FileConfiguration config = plugin.getConfig();
		if (cmd.getName().equalsIgnoreCase("fly")) {
			if (args.length == 0) {
				if (!(sender instanceof Player)) {
					return true;
				}
				Player player = (Player) sender;
				FlyGUI.flyGui(player);
			}
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("set")) {
					if (sender.hasPermission("timedfly.admin") || sender.hasPermission("timedfly.fly.set")) {
						try {
							Player player = Bukkit.getPlayer(args[1]);
							int time = Integer.parseInt(args[2]);
							player.setAllowFlight(true);
							GUIListener.cooldown.put(player.getUniqueId(), time * 60);
							sender.sendMessage(
									ChatColor.translateAlternateColorCodes('&', config.getString("Fly.Message.ToPlayer")
											.replace("%target%", player.getName()).replace("%time%", "" + time)));
							player.sendMessage(
									ChatColor.translateAlternateColorCodes('&',
											config.getString("Fly.Message.FromPlayer")
													.replace("%player%", sender.getName()).replace("%time%",
															"" + time)));
						} catch (Exception e) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
									"&cTimedFly >> &7Usage: /fly set <player> <minutes>"));
						}
					} else {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								config.getString("Messages.NoPermission.Message")));
						if (sender instanceof Player) {
							Player player = (Player) sender;
							Title title = new Title(
									ChatColor.translateAlternateColorCodes('&',
											config.getString("Messages.NoPermission.Title")),
									ChatColor.translateAlternateColorCodes('&',
											config.getString("Messages.NoPermission.SubTitle")),
									1, 2, 1);
							title.setTimingsToSeconds();
							title.send(player);
						}
						return true;
					}
				}
				if (args[0].equalsIgnoreCase("on")) {
					if (!(sender instanceof Player)) {
						return true;
					}
					Player player = (Player) sender;
					if (player.hasPermission("timedfly.admin") || player.hasPermission("timedfly.fly.onoff")) {
						player.setAllowFlight(true);
						player.sendMessage(
								ChatColor.translateAlternateColorCodes('&', config.getString("Fly.Message.SetOn")));
						return true;
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&',
								config.getString("Messages.NoPermission.Message")));
						Title title = new Title(
								ChatColor.translateAlternateColorCodes('&',
										config.getString("Messages.NoPermission.Title")),
								ChatColor.translateAlternateColorCodes('&',
										config.getString("Messages.NoPermission.SubTitle")),
								1, 2, 1);
						title.setTimingsToSeconds();
						title.send(player);

						return true;
					}
				}
				if (args[0].equalsIgnoreCase("off")) {
					if (!(sender instanceof Player)) {
						return true;
					}
					Player player = (Player) sender;
					if (player.hasPermission("timedfly.admin") || player.hasPermission("timedfly.fly.onoff")) {
						player.setAllowFlight(false);
						player.sendMessage(
								ChatColor.translateAlternateColorCodes('&', config.getString("Fly.Message.SetOff")));
						return true;
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&',
								config.getString("Messages.NoPermission.Message")));

						Title title = new Title(
								ChatColor.translateAlternateColorCodes('&',
										config.getString("Messages.NoPermission.Title")),
								ChatColor.translateAlternateColorCodes('&',
										config.getString("Messages.NoPermission.SubTitle")),
								1, 2, 1);
						title.setTimingsToSeconds();
						title.send(player);

						return true;
					}
				}
				if (args[0].equalsIgnoreCase("timeleft")) {
					if (!(sender instanceof Player)) {
						return true;
					}
					Player player = (Player) sender;
					if (GUIListener.cooldown.containsKey(player.getUniqueId())) {
						Integer millis = GUIListener.cooldown.get(player.getUniqueId()) * 1000;
						TimeZone tz = TimeZone.getTimeZone("UTC");
						SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
						df.setTimeZone(tz);
						String timel = df.format(new Date(millis));
						player.sendMessage(ChatColor.translateAlternateColorCodes('&',
								config.getString("Fly.Message.TimeLeft").replace("%timeleft%", timel)));
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&',
								config.getString("Fly.Message.TimeLeft").replace("%timeleft%", "00:00:00")));
					}
				}
			}
		}
		return true;
	}

}
