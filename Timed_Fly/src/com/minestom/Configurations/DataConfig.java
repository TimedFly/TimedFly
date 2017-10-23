package com.minestom.Configurations;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class DataConfig {

	private DataConfig() {
	}

	static DataConfig instance = new DataConfig();

	public static DataConfig getInstance() {
		return instance;
	}

	Plugin p;

	FileConfiguration data;
	File dfile;

	public void setup(Plugin p) {

		dfile = new File(p.getDataFolder(), "Data.yml");

		if (!dfile.exists()) {
			try {
				dfile.createNewFile();
			} catch (IOException e) {
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "TimedFly >> Could not create Data.yml!");
			}
		}

		data = YamlConfiguration.loadConfiguration(dfile);
	}

	public FileConfiguration getData() {
		return data;
	}

	public void saveData() {
		try {
			data.save(dfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "TimedFly >> Could not save Data.yml!");
		}
	}

	public void reloadData() {
		data = YamlConfiguration.loadConfiguration(dfile);
	}

	public PluginDescriptionFile getDesc() {
		return p.getDescription();
	}

}