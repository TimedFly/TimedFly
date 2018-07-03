package com.timedfly.configurations;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class ItemsConfig {

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

    public FileConfiguration getItemsConfig() {
        return this.items;
    }

    public void reloadItemsConfig() {
        saveItemsConfig();
        items = YamlConfiguration.loadConfiguration(itemsf);
    }

    public void saveItemsConfig() {
        try {
            items.save(itemsf);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().severe(ChatColor.RED + "TimedFly >> Could not save items.yml!");
        }
    }

}