package com.minestom.Utilities;

import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.minestom.TimedFly;

import net.milkbowl.vault.economy.Economy;

public class FlyGUI {

	public static TimedFly plugin = TimedFly.getPlugin(TimedFly.class);
	private static Economy economy = TimedFly.economy;

	@SuppressWarnings("deprecation")
	public static void flyGui(Player player) {
		FileConfiguration config = plugin.getConfig();
		Inventory inv = Bukkit.createInventory(player, config.getInt("Gui.Slots"),
				ChatColor.translateAlternateColorCodes('&', config.getString("Gui.DisplayName")));
		ConfigurationSection section = config.getConfigurationSection("Gui.Items");
		for (String string : section.getKeys(false)) {
			String price = Integer.toString(config.getInt("Gui.Items." + string + ".Price"));
			String time = Integer.toString(config.getInt("Gui.Items." + string + ".Time"));
			List<String> lore = new ArrayList<>();
			List<String> l = config.getStringList("Gui.Items." + string + ".Lore");
			ItemStack item = new ItemStack(Material.getMaterial(config.getInt("Gui.Items." + string + ".Material")),
					config.getInt("Gui.Items." + string + ".Ammount"),
					(short) config.getInt("Gui.Items." + string + ".Data"));
			ItemMeta meta = item.getItemMeta();
			if (!GUIListener.cooldown.containsKey(player.getUniqueId())) {
				int i = (int) Math.round(economy.getBalance(player));
				String format = NumberFormat.getIntegerInstance().format(i);
				for (String list : l) {
					lore.add(ChatColor.translateAlternateColorCodes('&', list).replace("%time%", time)
							.replace("%price%", price).replace("%timeleft%", "00:00:00")
							.replace("%balance%", "" + format));
				}
			} else {
				Integer millis = GUIListener.cooldown.get(player.getUniqueId()) * 1000;
				TimeZone tz = TimeZone.getTimeZone("UTC");
				SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
				df.setTimeZone(tz);
				String timel = df.format(new Date(millis));
				int i = (int) Math.round(economy.getBalance(player));
				String format = NumberFormat.getIntegerInstance().format(i);
				for (String list : l) {
					lore.add(ChatColor.translateAlternateColorCodes('&', list).replace("%time%", time)
							.replace("%price%", price).replace("%timeleft%", timel).replace("%balance%", "" + format));
				}

			}
			if (config.contains("Gui.Items." + string + ".Name")) {
				meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', config
						.getString("Gui.Items." + string + ".Name").replace("%time%", time).replace("%price%", price)));
			}
			if (config.contains("Gui.Items." + string + ".Lore")) {
				meta.setLore(lore);
			}
			if (config.getBoolean("Gui.Items." + string + ".Hide_Attributes") == true) {
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			}
			if (config.getBoolean("Gui.Items." + string + ".Hide_Enchants") == true) {
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			if (config.getBoolean("Gui.Items." + string + ".Hide_Place_On") == true) {
				meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
			}
			if (config.getBoolean("Gui.Items." + string + ".Hide_Potion_Effects") == true) {
				meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			}
			if (config.getBoolean("Gui.Items." + string + ".Hide_Unbreakable") == true) {
				meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			}
			if (config.getBoolean("Gui.Items." + string + ".Glow") == true) {
				meta.addEnchant(Enchantment.DURABILITY, 1, true);
			}
			item.setItemMeta(meta);
			inv.setItem(config.getInt("Gui.Items." + string + ".Slot"), item);
		}
		player.openInventory(inv);
	}

}
