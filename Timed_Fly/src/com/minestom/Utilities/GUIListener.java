package com.minestom.Utilities;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import com.minestom.TimedFly;

import be.maximvdw.titlemotd.ui.Title;
import net.milkbowl.vault.economy.Economy;

public class GUIListener implements Listener {

	public static HashMap<UUID, Integer> cooldown = new HashMap<UUID, Integer>();
	public static HashMap<UUID, Integer> godmode = new HashMap<UUID, Integer>();
	private Economy economy = TimedFly.economy;
	public TimedFly plugin = TimedFly.getPlugin(TimedFly.class);

	public GUIListener() {
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				for (Map.Entry<UUID, Integer> entry : cooldown.entrySet()) {
					Player player = Bukkit.getPlayer(entry.getKey());
					Integer time = entry.getValue();
					if (time == 0) {
						if (player == null) {
							cooldown.remove(entry.getKey());
						} else {
							if (player != null) {
								godmode.put(player.getUniqueId(), 6);
								cooldown.remove(player.getUniqueId());
								player.setAllowFlight(false);
								player.setFlying(false);
								player.playSound(player.getLocation(),
										Sound.valueOf(plugin.getConfig().getString("Fly.DisabledSound")), 100, 1);
								player.sendMessage(ChatColor.translateAlternateColorCodes('&',
										plugin.getConfig().getString("Fly.Message.Disabled")));
								if (plugin.getConfig().getBoolean("Fly.Titles.Disabled.Use") == true) {
									if (Bukkit.getVersion().contains("1.8")) {
										Title title = new Title(
												ChatColor.translateAlternateColorCodes('&',
														plugin.getConfig().getString("Fly.Titles.Disabled.Title")
																.replace("%time%", Integer.toString((int) (time - 1)))),
												ChatColor.translateAlternateColorCodes('&',
														plugin.getConfig().getString("Fly.Titles.Disabled.SubTitle")
																.replace("%time%", Integer.toString((int) (time - 1)))),
												1, 2, 1);
										title.send(player);
									} else {
										player.sendTitle(
												ChatColor.translateAlternateColorCodes('&',
														plugin.getConfig().getString("Fly.Titles.Disabled.Title")
																.replace("%time%", Integer.toString((int) (time - 1)))),
												ChatColor.translateAlternateColorCodes('&',
														plugin.getConfig().getString("Fly.Titles.Disabled.SubTitle")
																.replace("%time%", Integer.toString((int) (time - 1)))),
												20, 40, 20);
									}
								}
							}
						}
					} else {
						cooldown.put(entry.getKey(), time - 1);
						if (player != null) {
							if (player.getOpenInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&',
									plugin.getConfig().getString("Gui.DisplayName")))) {
								FlyGUI.flyGui(player);
							}
							if (Bukkit.getServer().getPluginManager().isPluginEnabled("ActionBarAPI") == true) {
								Integer millis = GUIListener.cooldown.get(player.getUniqueId()) * 1000;
								TimeZone tz = TimeZone.getTimeZone("UTC");
								SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
								df.setTimeZone(tz);
								String timel = df.format(new Date(millis));
								ActionBarAPI.sendActionBar(player, ChatColor.translateAlternateColorCodes('&', plugin
										.getConfig().getString("Fly.ActionBar.Message").replace("%timeleft%", timel)));
							}

							List<String> announce = plugin.getConfig().getStringList("Announce.Times");
							for (String list : announce) {
								if (time - 1 == Integer.parseInt(list)) {
									player.playSound(player.getLocation(),
											Sound.valueOf(plugin.getConfig().getString("Announce.Sound")), 100, 1);
									if (plugin.getConfig().getBoolean("Announce.Chat.Enabled") == true) {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&',
												plugin.getConfig().getString("Announce.Chat.Message")
														.replace("%seconds%", Integer.toString((int) (time - 1)))));
									}
									if (plugin.getConfig().getBoolean("Announce.Titles.Enabled") == true) {
										if (Bukkit.getVersion().contains("1.8")) {
											Title title = new Title(ChatColor.translateAlternateColorCodes('&',
													plugin.getConfig().getString("Announce.Titles.Title")
															.replace("%seconds%", Integer.toString((int) (time - 1)))),
													ChatColor.translateAlternateColorCodes('&',
															plugin.getConfig().getString("Announce.Titles.SubTitle")
																	.replace("%seconds%",
																			Integer.toString((int) (time - 1)))),
													1, 2, 1);
											title.send(player);
										} else {
											player.sendTitle(ChatColor.translateAlternateColorCodes('&',
													plugin.getConfig().getString("Announce.Titles.Title")
															.replace("%seconds%", Integer.toString((int) (time - 1)))),
													ChatColor.translateAlternateColorCodes('&',
															plugin.getConfig().getString("Announce.Titles.SubTitle")
																	.replace("%seconds%",
																			Integer.toString((int) (time - 1)))),
													5, 40, 5);
										}
									}
								}
							}
						}
					}
				}
			}
		}, 0, 20L);

	}

	@EventHandler
	public void flyListenerGui(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory inv = event.getClickedInventory();
		int slot = event.getSlot();
		FileConfiguration config = plugin.getConfig();
		if (inv.getTitle().equals(ChatColor.translateAlternateColorCodes('&', config.getString("Gui.DisplayName")))) {
			event.setCancelled(true);
			ConfigurationSection section = config.getConfigurationSection("Gui.Items");
			for (String string : section.getKeys(false)) {
				try {
					if (slot == config.getInt("Gui.Items." + string + ".Slot")) {
						if (event.getCurrentItem().hasItemMeta()) {
							if (player.hasPermission(config.getString("Gui.Items." + string + ".Permission"))) {
								int price = config.getInt("Gui.Items." + string + ".Price");
								int time = config.getInt("Gui.Items." + string + ".Time");
								if (!cooldown.containsKey(player.getUniqueId())) {
									if (economy.has(player, price)) {
										economy.withdrawPlayer(player, price);
									} else {
										player.closeInventory();
										player.sendMessage(ChatColor.translateAlternateColorCodes('&',
												config.getString("Fly.Message.NoMoney")
														.replace("%price%", Integer.toString(price))
														.replace("%time%", Integer.toString(time))));
										return;
									}
									player.setAllowFlight(true);
									cooldown.put(player.getUniqueId(), time * 60);
								} else {
									if (economy.has(player, price)) {
										economy.withdrawPlayer(player, price);
									} else {
										player.closeInventory();
										player.sendMessage(ChatColor.translateAlternateColorCodes('&',
												config.getString("Fly.Message.NoMoney")
														.replace("%price%", Integer.toString(price))
														.replace("%time%", Integer.toString(time))));
										return;
									}
									cooldown.put(player.getUniqueId(), cooldown.get(player.getUniqueId()) + time * 60);
								}
								player.sendMessage(ChatColor.translateAlternateColorCodes('&',
										config.getString("Fly.Message.Enabled")
												.replace("%price%", Integer.toString(price))
												.replace("%time%", Integer.toString(time))));
								if (plugin.getConfig().getBoolean("Fly.Titles.Enabled.Use") == true) {
									if (Bukkit.getVersion().contains("1.8")) {
										Title title = new Title(
												ChatColor.translateAlternateColorCodes('&',
														config.getString("Fly.Titles.Enabled.Title").replace("%time%",
																Integer.toString(time))),
												ChatColor
														.translateAlternateColorCodes('&',
																config.getString("Fly.Titles.Enabled.SubTitle")
																		.replace("%time%", Integer.toString(time))),
												1, 2, 1);
										title.send(player);
									} else {
										player.sendTitle(
												ChatColor.translateAlternateColorCodes('&',
														config.getString("Fly.Titles.Enabled.Title").replace("%time%",
																Integer.toString(time))),
												ChatColor.translateAlternateColorCodes('&',
														config.getString("Fly.Titles.Enabled.SubTitle")
																.replace("%time%", Integer.toString(time))),
												20, 40, 20);
									}
								}
								player.closeInventory();
							} else {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&',
										config.getString("Gui.Items." + string + ".PermissionMSG")));
								return;
							}
						}
					}
				} catch (Exception e) {

				}

			}
		}
	}

}
