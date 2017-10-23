package com.minestom.Configurations;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import com.minestom.TimedFly;

public class MessagesConfig {
	public TimedFly plugin = TimedFly.getPlugin(TimedFly.class);

	private MessagesConfig() {
	}

	static MessagesConfig instance = new MessagesConfig();

	public static MessagesConfig getInstance() {
		return instance;
	}

	Plugin p;

	FileConfiguration Lang;
	File dfile;

	public void initLangConfig() {

		this.getLang().addDefault("default", 10);
		this.getLang().addDefault("max", 50);

		this.getLang().options().copyDefaults(true);
		this.saveDefaultLangConfig();
		this.saveLang();

	}

	public void setup(Plugin p) {

		dfile = new File(p.getDataFolder(), "Lang.yml");

		if (!dfile.exists()) {
			try {
				dfile.createNewFile();
			} catch (IOException e) {
				Bukkit.getServer().getLogger().severe(ChatColor.RED + "TimedFly >> Could not create Lang.yml!");
			}
		}

		Lang = YamlConfiguration.loadConfiguration(dfile);
	}

	public FileConfiguration getLang() {
		return Lang;
	}

	public void saveLang() {
		try {
			Lang.save(dfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "TimedFly >> Could not save Lang.yml!");
		}
	}

	public void reloadLang() {
		Lang = YamlConfiguration.loadConfiguration(dfile);
	}

	public PluginDescriptionFile getDesc() {
		return p.getDescription();
	}

	public void saveDefaultLangConfig() {
		if (dfile == null) {
			dfile = new File(plugin.getDataFolder(), "Lang.yml");
		}
		if (!dfile.exists()) {
			this.plugin.saveResource("Lang.yml", false);
		}
	}
}