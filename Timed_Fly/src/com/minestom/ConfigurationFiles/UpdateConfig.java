package com.minestom.ConfigurationFiles;

import com.minestom.TimedFly;
import com.minestom.Utilities.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UpdateConfig {

    private LangFiles lang = LangFiles.getInstance();

    public void updateConfig(TimedFly plugin) {
        Utility utility = new Utility(plugin);
        FileConfiguration configuration = plugin.getConfig();
        FileConfiguration language = lang.getLang();

        Set<String> path = configuration.getKeys(false);

        if (!path.contains("OnFlyDisableCommands")) {
            List<String> commands = new ArrayList<>();
            commands.add("[console] say %player% is using hacks D:");
            commands.add("[player] say i'm using hacks >:D");
            configuration.set("OnFlyDisableCommands.Enabled", false);
            configuration.set("OnFlyDisableCommands.Commands", commands);
            utility.message(Bukkit.getConsoleSender(), "&cYour Configuration file has some sections missing. Updating...");
            plugin.saveConfig();
        }
        if (!path.contains("UseLevelsCurrency")) {
            configuration.set("UseLevelsCurrency", false);
            utility.message(Bukkit.getConsoleSender(), "&cYour Configuration file has some sections missing. Updating...");
            plugin.saveConfig();
        }
        if (!path.contains("FlyModeIfHasPerm")) {
            configuration.set("FlyModeIfHasPerm", true);
            utility.message(Bukkit.getConsoleSender(), "&cYour Configuration file has some sections missing. Updating...");
            plugin.saveConfig();
        }
        if (!path.contains("Sounds")) {
            configuration.set("Sounds.Enabled", true);
            configuration.set("Sounds.Announcer", "ENTITY_EXPERIENCE_ORB_PICKUP");
            configuration.set("Sounds.FlightDisabled", "ENTITY_WITHER_DEATH");
            utility.message(Bukkit.getConsoleSender(), "&cYour Configuration file has some sections missing. Updating...");
            plugin.saveConfig();
        }
        if (!path.contains("Announcer")) {
            List<String> times = new ArrayList<>();
            times.add("120");
            times.add("60");
            times.add("30");
            times.add("10");
            times.add("5");
            times.add("4");
            times.add("3");
            times.add("2");
            times.add("1");
            times.add("0");
            configuration.set("Announcer.Chat", false);
            configuration.set("Announcer.Titles", true);
            configuration.set("Announcer.Times", times);
            utility.message(Bukkit.getConsoleSender(), "&cYour Configuration file has some sections missing. Updating...");
            plugin.saveConfig();
        }
        if (!path.contains("BossBarTimer")) {
            configuration.set("BossBarTimer.Enabled", false);
            configuration.set("BossBarTimer.Color", "RED");
            configuration.set("BossBarTimer.Style", "SOLID");
            utility.message(Bukkit.getConsoleSender(), "&cYour Configuration file has some sections missing. Updating...");
            plugin.saveConfig();
        }
        if (language.getString("Fly.Message.NoLevels") == null) {
            language.set("Fly.Message.NoLevels", "&aYou don't have enough &eLevels &ato buy this. You need &e%levels_needed%");
            utility.message(Bukkit.getConsoleSender(), "&cYour Language file has some sections missing. Updating...");
            lang.saveLang();
        }
        if (language.getString("Other.NoCurrencyFound") == null) {
            language.set("Other.NoCurrencyFound", "&7Could not find a currency to perform this action. If you are the server admin please enable one in the configuration file.");
            utility.message(Bukkit.getConsoleSender(), "&cYour Language file has some sections missing. Updating...");
            lang.saveLang();
        }
        if (language.getString("Fly.BossBar.Message") == null) {
            language.set("Fly.BossBar.Message", "&aYou have %timeleft% of fly time");
            utility.message(Bukkit.getConsoleSender(), "&cYour Language file has some sections missing. Updating...");
            lang.saveLang();
        }
    }
}