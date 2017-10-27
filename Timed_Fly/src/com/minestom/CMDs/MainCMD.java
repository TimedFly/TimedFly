package com.minestom.CMDs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.minestom.TimedFly;
import com.minestom.Languages.ItemsConfig;
import com.minestom.Languages.LangConfig;

import be.maximvdw.titlemotd.ui.Title;
import mkremins.fanciful.FancyMessage;

public class MainCMD implements CommandExecutor {

	public static TimedFly plugin = TimedFly.getPlugin(TimedFly.class);
	private LangConfig lang = LangConfig.getInstance();
	private ItemsConfig items = ItemsConfig.getInstance();

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
		if (cmd.getName().equalsIgnoreCase("timedfly")) {
			FileConfiguration config = lang.getLang();
			FileConfiguration itemscf = items.getItems();
			if (args.length == 0) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cTimedFly &7created by &cBy_Jack"));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
				sender.sendMessage(
						ChatColor.translateAlternateColorCodes('&', "&7To see all commands available use &e/tf help"));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
				return true;
			}
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("help")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l             TimedFly"));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&e/tf help &7- show this help page"))
							.tooltip("¡ìaClick to execute /tf help").command("/tf help").send(sender);
					new FancyMessage(
							ChatColor.translateAlternateColorCodes('&', "&e/tf reload &7- reloads the config file"))
									.tooltip("¡ìaClick to execute /tf reload").command("/tf reload").send(sender);
					new FancyMessage(ChatColor.translateAlternateColorCodes('&',
							"&e/tf setTime <itemID> <minutes> &7- create a new timed fly"))
									.tooltip("¡ìaClick to execute /tf setTime <itemID> <minutes>").insert("/tf setTime")
									.send(sender);
					new FancyMessage(ChatColor.translateAlternateColorCodes('&',
							"&e/tf setPrice <itemID> <price> &7- create a new timed fly"))
									.tooltip("¡ìaClick to execute /tf setPrice <itemID> <price>").insert("/tf setPrice")
									.send(sender);
					new FancyMessage(ChatColor.translateAlternateColorCodes('&',
							"&e/tf setItem <itemID> &7- set the item that you have on hand"))
									.tooltip("¡ìaClick to execute /tf setItem <itemID>").insert("/tf setItem <itemID>")
									.send(sender);
					new FancyMessage(ChatColor.translateAlternateColorCodes('&',
							"&e/tf permissions &7- see all available permissions"))
									.tooltip("¡ìaClick to execute /tf permissions").command("/tf permissions")
									.send(sender);
					new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&e/tf list &7- see all the ItemID"))
							.tooltip("¡ìaClick to execute /tf list").command("/tf list").send(sender);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&7Pro Tip: You can hover over the commands and click them"));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					new FancyMessage(ChatColor.translateAlternateColorCodes('&', "                              "))
							.then("¡ìe>>>").tooltip("¡ìaNext Page").command("/tf help2").send(sender);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&7                 Version: &c" + plugin.getDescription().getVersion()));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					return true;
				}
				if (args[0].equalsIgnoreCase("help2")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l             TimedFly"));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&e/tf help2 &7- show this help page"))
							.tooltip("¡ìaClick to execute /tf help").command("/tf help2").send(sender);
					new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&e/fly &7- opens the fly menu"))
							.tooltip("¡ìaClick to execute /fly").command("/fly").send(sender);
					new FancyMessage(ChatColor.translateAlternateColorCodes('&',
							"&e/fly set <player> <minutes> &7- set fly mode to another player, no cost"))
									.tooltip("¡ìaClick to execute /fly set <player> <minutes>")
									.insert("/fly set <player> <minutes>").send(sender);
					new FancyMessage(ChatColor.translateAlternateColorCodes('&',
							"&e/fly on &7- set fly mode to yourself, no time nor cost"))
									.tooltip("¡ìaClick to execute /fly on").command("/fly on").send(sender);
					new FancyMessage(
							ChatColor.translateAlternateColorCodes('&', "&e/fly off &7- unset fly mode to yourself"))
									.tooltip("¡ìaClick to execute /fly off").command("/fly off").send(sender);
					new FancyMessage(ChatColor.translateAlternateColorCodes('&',
							"&e/fly timeleft &7- check how much time do you have left"))
									.tooltip("¡ìaClick to execute /fly timeleft").command("/fly timeleft").send(sender);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&7Pro Tip: You can hover over the commands and click them"));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					new FancyMessage(ChatColor.translateAlternateColorCodes('&', "                   ")).then("¡ìe<<<")
							.tooltip("¡ìaPrevious Page").command("/tf help").send(sender);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&7                 Version: &c" + plugin.getDescription().getVersion()));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					return true;
				}
				if (args[0].equalsIgnoreCase("reload")) {
					if (sender.hasPermission("timedfly.admin")) {
						plugin.reloadConfig();
						lang.reloadLang();
						lang.createFiles(plugin);
						items.createFiles(plugin);
						items.reloadItems();
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&cTimedFly >> &econfig.yml was succesfully reloaded."));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
								"&cTimedFly >> &eitems.yml was succesfully reloaded."));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cTimedFly >> &elang_"
								+ plugin.getConfig().getString("Lang") + ".yml was succesfully reloaded."));
						if (sender instanceof Player) {
							Player player = (Player) sender;
							if (Bukkit.getVersion().contains("1.8")) {
								Title title = new Title(
										ChatColor.translateAlternateColorCodes('&', "All files reloaded"), null, 1, 2,
										1);
								title.setTimingsToSeconds();
								title.send(player);
							} else {
								player.sendTitle(ChatColor.translateAlternateColorCodes('&', "All files reloaded"),
										null, 1 * 20, 2 * 20, 1 * 20);
							}
						}
						return true;
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
				if (args[0].equalsIgnoreCase("list")) {
					if (sender.hasPermission("timedfly.admin")) {
						ConfigurationSection section = itemscf.getConfigurationSection("Items");
						int i = 0;
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
						sender.sendMessage(
								ChatColor.translateAlternateColorCodes('&', "&c         TimedFly ItemID List"));
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
						for (String list : section.getKeys(false)) {
							i++;
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e" + i + ": &7" + list));
						}
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
						return true;
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
				if (args[0].equalsIgnoreCase("setTime")) {
					if (sender.hasPermission("timedfly.admin")) {
						try {
							int gettime = Integer.parseInt(args[1]);
							int time = Integer.parseInt(args[2]);
							if (itemscf.contains("Items." + gettime)) {
								itemscf.set("Items." + gettime + ".Time", time);
								items.saveItems();
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
										config.getString("Other.SetTime.Found")
												.replace("%time%", Integer.toString(time))
												.replace("%itemid%", Integer.toString(gettime))));
							} else {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
										config.getString("Other.SetTime.NotFound")
												.replace("%time%", Integer.toString(time))
												.replace("%itemid%", Integer.toString(gettime))));
							}

						} catch (Exception e) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
									config.getString("Other.SetTime.Usage")));
						}
						return true;
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
				if (args[0].equalsIgnoreCase("setPrice")) {
					if (sender.hasPermission("timedfly.admin")) {
						try {
							int getprice = Integer.parseInt(args[1]);
							int price = Integer.parseInt(args[2]);
							if (itemscf.contains("Items." + getprice)) {
								itemscf.set("Items." + getprice + ".Price", price);
								items.saveItems();
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
										config.getString("Other.SetPrice.Found")
												.replace("%price%", Integer.toString(price))
												.replace("%itemid%", Integer.toString(getprice))));
							} else {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
										config.getString("Other.SetPrice.NotFound")
												.replace("%price%", Integer.toString(price))
												.replace("%itemid%", Integer.toString(getprice))));
							}

						} catch (Exception e) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
									config.getString("Other.SetPrice.Usage")));
						}
						return true;
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
				if (args[0].equalsIgnoreCase("permissions")) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					sender.sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&c&l             TimedFly Permissions"));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					sender.sendMessage(
							ChatColor.translateAlternateColorCodes('&', "&etimedfly.admin &7- access to all commands"));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&etimedfly.fly.set &7-  access to set fly to another player"));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
							"&etimedfly.fly.onoff &7- access to enable or disable fly for your self"));
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', ""));
					return true;
				}
				if (args[0].equalsIgnoreCase("setItem")) {
					if (!(sender instanceof Player)) {
						return true;
					}
					if (sender.hasPermission("timedfly.admin")) {
						Player player = (Player) sender;
						try {
							int itemid = Integer.parseInt(args[1]);
							int data = player.getItemInHand().getDurability();
							int ammount = player.getItemInHand().getAmount();
							int id = player.getItemInHand().getTypeId();
							if (itemscf.contains("Items." + itemid)) {
								itemscf.set("Items." + itemid + ".Material", id);
								itemscf.set("Items." + itemid + ".Data", data);
								itemscf.set("Items." + itemid + ".Ammount", ammount);
								items.saveItems();
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
										config.getString("Other.SetItem.Found").replace("%itemid%",
												Integer.toString(itemid))));
							} else {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
										config.getString("Other.SetItem.NotFound").replace("%itemid%",
												Integer.toString(itemid))));
							}

						} catch (Exception e) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
									config.getString("Other.SetItem.Usage")));
						}

						return true;
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
			}
		}
		return true;
	}

}
