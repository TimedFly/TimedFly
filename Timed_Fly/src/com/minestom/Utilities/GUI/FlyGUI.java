package com.minestom.Utilities.GUI;

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
import com.minestom.Languages.ItemsConfig;

import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;

public class FlyGUI {

	public static TimedFly plugin = TimedFly.getPlugin(TimedFly.class);
	private static Economy economy = TimedFly.economy;
	private static ItemsConfig items = ItemsConfig.getInstance();

	@SuppressWarnings("deprecation")
	public static void flyGui(Player player) {
		FileConfiguration config = plugin.getConfig();
		FileConfiguration itemscf = items.getItems();
		Inventory inv = Bukkit.createInventory(player, config.getInt("Gui.Slots"),
				ChatColor.translateAlternateColorCodes('&', config.getString("Gui.DisplayName")));
		ConfigurationSection section = itemscf.getConfigurationSection("Items");
		for (String string : section.getKeys(false)) {
			String price = Integer.toString(itemscf.getInt("Items." + string + ".Price"));
			String time = Integer.toString(itemscf.getInt("Items." + string + ".Time"));
			List<String> lore = new ArrayList<>();
			List<String> l = itemscf.getStringList("Items." + string + ".Lore");
			ItemStack item = new ItemStack(Material.getMaterial(itemscf.getInt("Items." + string + ".Material")),
					itemscf.getInt("Items." + string + ".Ammount"),
					(short) itemscf.getInt("Items." + string + ".Data"));
			ItemMeta meta = item.getItemMeta();
			if (!GUIListener.cooldown.containsKey(player.getUniqueId())) {
				int i = (int) Math.round(economy.getBalance(player));
				String format = NumberFormat.getIntegerInstance().format(i);
				for (String list : l) {
					String loreNoPAPI = ChatColor.translateAlternateColorCodes('&', list).replace("%time%", time)
							.replace("%price%", price).replace("%timeleft%", "00:00:00")
							.replace("%balance%", "" + format);
					String lorePAPI = PlaceholderAPI.setPlaceholders(player, loreNoPAPI);
					lore.add(lorePAPI);
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
					if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") == true) {
						String loreNoPAPI = ChatColor.translateAlternateColorCodes('&', list).replace("%time%", time)
								.replace("%price%", price).replace("%timeleft%", timel)
								.replace("%balance%", "" + format);
						String lorePAPI = PlaceholderAPI.setPlaceholders(player, loreNoPAPI);
						lore.add(lorePAPI);
					} else {
						lore.add(ChatColor.translateAlternateColorCodes('&', list).replace("%time%", time)
								.replace("%price%", price).replace("%timeleft%", timel)
								.replace("%balance%", "" + format));
					}
				}

			}
			if (itemscf.contains("Items." + string + ".Name")) {
				if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") == true) {
					String nameNoPAPI = ChatColor.translateAlternateColorCodes('&', itemscf
							.getString("Items." + string + ".Name").replace("%time%", time).replace("%price%", price));
					String namePAPI = PlaceholderAPI.setPlaceholders(player, nameNoPAPI);
					meta.setDisplayName(namePAPI);
				} else {
					meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemscf
							.getString("Items." + string + ".Name").replace("%time%", time).replace("%price%", price)));
				}
			}
			if (itemscf.contains("Items." + string + ".Lore")) {
				meta.setLore(lore);
			}
			if (itemscf.getBoolean("Items." + string + ".Hide_Attributes") == true) {
				meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			}
			if (itemscf.getBoolean("Items." + string + ".Hide_Enchants") == true) {
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			if (itemscf.getBoolean("Items." + string + ".Hide_Place_On") == true) {
				meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
			}
			if (itemscf.getBoolean("Items." + string + ".Hide_Potion_Effects") == true) {
				meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			}
			if (itemscf.getBoolean("Items." + string + ".Hide_Unbreakable") == true) {
				meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			}
			if (itemscf.getBoolean("Items." + string + ".Glow") == true) {
				meta.addEnchant(Enchantment.DURABILITY, 1, true);
			}
			item.setItemMeta(meta);
			inv.setItem(itemscf.getInt("Items." + string + ".Slot"), item);
		}
		player.openInventory(inv);
	}

}
