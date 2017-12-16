package com.minestom.ConfigurationFiles;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class LangFiles {

	private LangFiles() {
	}

	static LangFiles instance = new LangFiles();

	public static LangFiles getInstance() {
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
		}
		File langEn = new File(p.getDataFolder() + File.separator + "languages" + File.separator, "lang_en.yml");
		File langSp = new File(p.getDataFolder() + File.separator + "languages" + File.separator, "lang_sp.yml");
		File langHu = new File(p.getDataFolder() + File.separator + "languages" + File.separator, "lang_hu.yml");
		File langDe = new File(p.getDataFolder() + File.separator + "languages" + File.separator, "lang_de.yml");
		if (!langEn.exists()) {
			p.saveResource("languages/lang_en.yml", false);
			Bukkit.getServer().getConsoleSender()
					.sendMessage(ChatColor.RED + "TimedFly >>" + ChatColor.GRAY + " Creating new file, lang_en.yml!");
		}
		if (!langSp.exists()) {
			p.saveResource("languages/lang_sp.yml", false);
			Bukkit.getServer().getConsoleSender()
					.sendMessage(ChatColor.RED + "TimedFly >>" + ChatColor.GRAY + " Creating new file, lang_sp.yml!");
		}
		if (!langHu.exists()) {
			p.saveResource("languages/lang_hu.yml", false);
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "TimedFly >>" + ChatColor.GRAY
					+ " Creating new file, lang_hu.yml! Translated by Toldi.");
		}
		if (!langDe.exists()) {
			p.saveResource("languages/lang_de.yml", false);
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "TimedFly >>" + ChatColor.GRAY
					+ " Creating new file, lang_de.yml! Translated by False.");
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
			Bukkit.getServer().getConsoleSender()
					.sendMessage(ChatColor.RED + "TimedFly >> Could not save the languages files!");
		}
	}

}