package com.minestom.Utilities.Others;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.minestom.ConfigurationFiles.ItemsConfig;

import me.clip.placeholderapi.PlaceholderAPI;

public class OnClick {
	private static ItemsConfig items = ItemsConfig.getInstance();

	public static void clickEvent(Player player, String string) {
		FileConfiguration itemscf = items.getItems();
		List<String> clickevent = itemscf.getStringList("Items." + string + ".OnClick");
		for (String click : clickevent) {
			if (click.contains("[message]")) {
				if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") == true) {
					String msgNoPAPI = ChatColor.translateAlternateColorCodes('&', click.replace("[message] ", ""));
					String msgPAPI = PlaceholderAPI.setPlaceholders(player, msgNoPAPI);
					player.sendMessage(msgPAPI);
				} else {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', click.replace("[message] ", "")));
				}
			}
			if (click.contains("[player]")) {
				Bukkit.dispatchCommand(player, click.replace("[player] ", "").replace("%player%", player.getName()));
			}
			if (click.contains("[console]")) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						click.replace("[console] ", "").replace("%player%", player.getName()));

			}
			if (click.contains("[sound]")) {
				player.playSound(player.getLocation(), Sound.valueOf(click.replace("[sound] ", "")), 100, 1);
			}
			if (click.contains("[close]")) {
				player.closeInventory();
			}
		}
	}
}
