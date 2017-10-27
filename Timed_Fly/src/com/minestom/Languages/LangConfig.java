package com.minestom.Languages;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class LangConfig {

	private LangConfig() {
	}

	static LangConfig instance = new LangConfig();

	public static LangConfig getInstance() {
		return instance;
	}

	Plugin p;

	private File langf;
	private FileConfiguration lang;

	public void createFiles(Plugin p) {
		langf = new File(p.getDataFolder() + File.separator + "languages" + File.separator,
				"lang_" + p.getConfig().getString("Lang") + ".yml");

		if (!langf.exists()) {
			langf.getParentFile().mkdirs();
			p.saveResource("languages/lang_en.yml", false);
			p.saveResource("languages/lang_sp.yml", false);
		}
		lang = new YamlConfiguration();
		try {
			lang.load(langf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration getLang() {
		return this.lang;
	}

	public void reloadLang() {
		lang = YamlConfiguration.loadConfiguration(langf);
	}

	public void saveLang() {
		try {
			lang.save(langf);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(ChatColor.RED + "TimedFly >> Could not save the languages files!");
		}
	}

}