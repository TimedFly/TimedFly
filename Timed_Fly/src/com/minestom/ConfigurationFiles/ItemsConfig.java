package com.minestom.ConfigurationFiles;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ItemsConfig {

	private ItemsConfig() {
	}

	static ItemsConfig instance = new ItemsConfig();

	public static ItemsConfig getInstance() {
		return instance;
	}

	Plugin p;

	private File itemsf;
	private FileConfiguration items;

	public void createFiles(Plugin p) {
		itemsf = new File(p.getDataFolder(), "items.yml");

		if (!itemsf.exists()) {
			itemsf.getParentFile().mkdirs();
			p.saveResource("items.yml", false);
		}
		items = new YamlConfiguration();
		try {
			items.load(itemsf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration getItems() {
		return this.items;
	}

	public void reloadItems() {
		items = YamlConfiguration.loadConfiguration(itemsf);
	}

	public void saveItems() {
		try {
			items.save(itemsf);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "TimedFly >> Could not save items.yml!");
		}
	}

}