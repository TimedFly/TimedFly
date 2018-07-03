package com.timedfly.configurations;

import com.timedfly.utilities.Message;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Languages {

    private File langf;
    private FileConfiguration languageConfig;
    private static Map<String, String> format = new HashMap<>();

    public void createFiles(Plugin p) {
        langf = new File(p.getDataFolder() + File.separator + "languages" + File.separator,
                "lang_" + p.getConfig().getString("Lang") + ".yml");

        if (!langf.exists()) langf.getParentFile().mkdirs();

        File langEn = new File(p.getDataFolder() + File.separator + "languages" + File.separator, "lang_en.yml");
        File langSp = new File(p.getDataFolder() + File.separator + "languages" + File.separator, "lang_sp.yml");
        File langHu = new File(p.getDataFolder() + File.separator + "languages" + File.separator, "lang_hu.yml");
        File langDe = new File(p.getDataFolder() + File.separator + "languages" + File.separator, "lang_de.yml");
        File langVi = new File(p.getDataFolder() + File.separator + "languages" + File.separator, "lang_vi.yml");

        if (!langEn.exists()) {
            p.saveResource("languages/lang_en.yml", false);
            Message.sendConsoleMessage(ChatColor.GRAY + " Creating new file, lang_en.yml!");
        }
        if (!langSp.exists()) {
            p.saveResource("languages/lang_sp.yml", false);
            Message.sendConsoleMessage(ChatColor.GRAY + " Creating new file, lang_sp.yml!");
        }
        if (!langHu.exists()) {
            p.saveResource("languages/lang_hu.yml", false);
            Message.sendConsoleMessage(ChatColor.GRAY + " Creating new file, lang_hu.yml! Translated by Toldi.");
        }
        if (!langDe.exists()) {
            p.saveResource("languages/lang_de.yml", false);
            Message.sendConsoleMessage(ChatColor.GRAY + " Creating new file, lang_de.yml! Translated by False.");
        }
        if (!langVi.exists()) {
            p.saveResource("languages/lang_vi.yml", false);
            Message.sendConsoleMessage(ChatColor.GRAY + " Creating new file, lang_vi.yml! Translated by JeckTN.");
        }
        languageConfig = new YamlConfiguration();
        try {
            languageConfig.load(langf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addFormatString();
    }

    public FileConfiguration getLanguageFile() {
        return this.languageConfig;
    }

    public void reloadLanguages() {
        saveLanguages();
        languageConfig = YamlConfiguration.loadConfiguration(langf);
        addFormatString();
    }

    private void saveLanguages() {
        try {
            languageConfig.save(langf);
        } catch (IOException e) {
            Message.sendConsoleMessage("&cTimedFly >> Could not save the languages files!");
        }
    }

    private void addFormatString() {
        format.put("Fly.BossBar", languageConfig.getString("Fly.BossBar"));
        format.put("Format.Plural.Days", languageConfig.getString("Format.Plural.Days"));
        format.put("Format.Plural.Hours", languageConfig.getString("Format.Plural.Hours"));
        format.put("Format.Plural.Minutes", languageConfig.getString("Format.Plural.Minutes"));
        format.put("Format.Plural.Seconds", languageConfig.getString("Format.Plural.Seconds"));
        format.put("Format.Singular.Day", languageConfig.getString("Format.Singular.Day"));
        format.put("Format.Singular.Hour", languageConfig.getString("Format.Singular.Hour"));
        format.put("Format.Singular.Minute", languageConfig.getString("Format.Singular.Minute"));
        format.put("Format.Singular.Second", languageConfig.getString("Format.Singular.Second"));
    }

    public static String getFormat(String path) {
        return format.get(path);
    }
}