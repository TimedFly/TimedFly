package com.minestom.ConfigurationFiles;

import com.minestom.TimedFly;
import com.minestom.Utilities.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public class UpdateConfig {

    private LangFiles lang = LangFiles.getInstance();

    public void updateConfig(TimedFly plugin) {
        Utility utility = new Utility(plugin);
        FileConfiguration configuration = plugin.getConfig();
        FileConfiguration language = lang.getLang();

        configuration.addDefault("Prefix", "&cTimedFly > ");
        configuration.addDefault("Cooldown", "1h");
        configuration.addDefault("Type", "sqlite");
        configuration.addDefault("MySQL.Database", "database");
        configuration.addDefault("MySQL.Host", "localhost");
        configuration.addDefault("MySQL.Port", "3306");
        configuration.addDefault("MySQL.Username", "user");
        configuration.addDefault("MySQL.Password", "pass");
        configuration.addDefault("OpenMenuCommand", "fly;flymenu");
        configuration.addDefault("Gui.DisplayName", "TimedFly");
        configuration.addDefault("Gui.Slots", 9);
        configuration.addDefault("StopTimerOnLeave", true);
        configuration.addDefault("BossBarTimer.Enabled", false);
        configuration.addDefault("BossBarTimer.Color", "GREEN");
        configuration.addDefault("BossBarTimer.Style", "SOLID");
        configuration.addDefault("JoinFlying.Enabled", true);
        configuration.addDefault("JoinFlying.Height", 2);
        configuration.addDefault("Sounds.Enabled", true);
        configuration.addDefault("Sounds.Announcer", "ENTITY_EXPERIENCE_ORB_PICKUP");
        configuration.addDefault("Sounds.FlightDisabled", "ENTITY_WITHER_DEATH");
        configuration.addDefault("ASkyblockIntegration", true);
        configuration.addDefault("UseTokenManager", false);
        configuration.addDefault("UseVault", true);
        configuration.addDefault("UseLevelsCurrency", false);
        configuration.addDefault("FlyModeIfHasPerm", true);
        configuration.addDefault("UsePermission.Use", false);
        configuration.addDefault("UsePermission.Permission", "timedfly.flygui");
        configuration.addDefault("LimitMaxTime", 30);
        configuration.addDefault("Announcer.Chat", false);
        configuration.addDefault("Announcer.Titles", true);
        configuration.addDefault("Announcer.Times", Arrays.asList("120", "60", "30", "15", "10", "5", "4", "3", "2", "1"));
        configuration.addDefault("OnFlyDisableCommands.Enabled", false);
        configuration.addDefault("OnFlyDisableCommands.Commands", Arrays.asList("[console] say %player% is using hacks D:", "[player] say i'm using hacks >:D"));
        configuration.addDefault("World-List.Type", "all");
        configuration.addDefault("World-List.Worlds", Arrays.asList("world", "survival"));
        configuration.addDefault("Check-For-Updates", true);
        configuration.addDefault("Auto-Download", false);
        configuration.addDefault("Lang", "en");
        configuration.options().copyDefaults(true);
        plugin.saveConfig();

        if (language.getString("Fly.Message.NoLevels") == null) {
            language.set("Fly.Message.NoLevels", "&aYou don't have enough &eLevels &ato buy this. You need &e%levels_needed%");
            utility.message(Bukkit.getConsoleSender(), "&cYour Language file has some sections missing. Updating...");
            lang.saveLang();
        }
        if (language.getString("Fly.Message.AddTimeToPlayer") == null) {
            language.set("Fly.Message.AddTimeToPlayer", "&aYou have been added &7%time% &aminutes to your flight time.");
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
        if (language.getString("Fly.Message.StopAndResume") == null) {
            language.set("Fly.Message.StopAndResume.Stop", "&7You have &cStopped &7the flight time!");
            language.set("Fly.Message.StopAndResume.Resume", "&7You have &aResumed &7the flight time!");
            language.set("Fly.Message.StopAndResume.NoTime", "&7You don't have any time left to do this!");
            language.set("Fly.Message.StopAndResume.Already", "&7You have done this already!");
            utility.message(Bukkit.getConsoleSender(), "&cYour Language file has some sections missing. Updating...");
            lang.saveLang();
        }
    }
}