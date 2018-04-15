package com.timedfly.ConfigurationFiles;

import com.timedfly.TimedFly;
import com.timedfly.Utilities.Utility;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;

public class UpdateConfig {

    private LangFiles lang = LangFiles.getInstance();

    public void updateConfig(TimedFly plugin) {
        Utility utility = plugin.getUtility();
        FileConfiguration configuration = plugin.getConfig();
        FileConfiguration language = lang.getLang();

        configuration.options().header(getHeader());
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
        configuration.addDefault("StopTimerOnBlackListedWorld", false);
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
        configuration.addDefault("Messages.Title", true);
        configuration.addDefault("Messages.ActionBar", true);
        configuration.addDefault("OnFlyDisableCommands.Enabled", false);
        configuration.addDefault("StopFlyOnAttack", true);
        configuration.addDefault("OnFlyDisableCommands.Commands", Arrays.asList("[console] say %player% is using hacks D:", "[player] say i'm using hacks >:D"));
        configuration.addDefault("DisableFlyOnGround", true);
        configuration.addDefault("World-List.Type", "all");
        configuration.addDefault("World-List.Worlds", Arrays.asList("world", "survival"));
        configuration.addDefault("Check-For-Updates", true);
        configuration.addDefault("Auto-Download", false);
        configuration.addDefault("Lang", "en");
        configuration.options().copyHeader(true).copyDefaults(true);
        plugin.saveConfig();

        if (language.getString("Fly.Message.NoLevels") == null) {
            language.set("Fly.Message.NoLevels", "&aYou don't have enough &eLevels &ato buy this. You need &e%levels_needed%");
            utility.message(Bukkit.getConsoleSender(), "&c[1] Your Language file has some sections missing. Updating...");
            lang.saveLang();
        }
        if (language.getString("Fly.Message.AddTimeToPlayer") == null) {
            language.set("Fly.Message.AddTimeToPlayer", "&aYou have been added &7%time% &aminutes to your flight time.");
            utility.message(Bukkit.getConsoleSender(), "&c[2] Your Language file has some sections missing. Updating...");
            lang.saveLang();
        }
        if (language.getString("Other.NoCurrencyFound") == null) {
            language.set("Other.NoCurrencyFound", "&7Could not find a currency to perform this action. If you are the server admin please enable one in the configuration file.");
            utility.message(Bukkit.getConsoleSender(), "&c[3] Your Language file has some sections missing. Updating...");
            lang.saveLang();
        }
        if (language.getString("Other.PlayerNotFound") == null) {
            language.set("Other.PlayerNotFound", "&7Could not find that player... Make sure the player is online.");
            utility.message(Bukkit.getConsoleSender(), "&c[4] Your Language file has some sections missing. Updating...");
            lang.saveLang();
        }
        if (language.getString("Fly.BossBar") == null) {
            language.set("Fly.BossBar", "&aYou have %timeleft% of fly time");
            utility.message(Bukkit.getConsoleSender(), "&c[5] Your Language file has some sections missing. Updating...");
            lang.saveLang();
        }
        if (language.getString("Fly.Message.OthersTimeLeft") == null) {
            language.set("Fly.BossBar.Message", "&a%player% has &7%timeleft% &atime left.");
            utility.message(Bukkit.getConsoleSender(), "&c[6] Your Language file has some sections missing. Updating...");
            lang.saveLang();
        }
        if (language.getString("Fly.Message.NoExp") == null) {
            language.set("Fly.Message.NoExp", "&aYou don't have enough &eLevels &ato buy this. You need &e%levels_needed%");
            utility.message(Bukkit.getConsoleSender(), "&c[7] Your Language file has some sections missing. Updating...");
            lang.saveLang();
        }
        if (language.getString("Other.OnCombat") == null) {
            language.set("Other.OnCombat", "&aYou have entered in combat. Disabling flight.");
            utility.message(Bukkit.getConsoleSender(), "&c[8] Your Language file has some sections missing. Updating...");
            lang.saveLang();
        }
        if (language.getString("Fly.Message.StopAndResume") == null) {
            language.set("Fly.Message.StopAndResume.Stop", "&7You have &cStopped &7the flight time!");
            language.set("Fly.Message.StopAndResume.Resume", "&7You have &aResumed &7the flight time!");
            language.set("Fly.Message.StopAndResume.NoTime", "&7You don't have any time left to do this!");
            language.set("Fly.Message.StopAndResume.Already", "&7You have done this already!");
            utility.message(Bukkit.getConsoleSender(), "&c[9] Your Language file has some sections missing. Updating...");
            lang.saveLang();
        }
    }

    private String getHeader() {
        return "#################################################\n" +
                "##                                              #\n" +
                "##                  Timed Fly                   #\n" +
                "##               Made by By_Jack                #\n" +
                "##                                              #\n" +
                "#################################################\n" +
                "#\n" +
                "## Use 'none' to disable\n" +
                "#Prefix: '&cTimedFly >> '\n" +
                "#\n" +
                "## Cooldown time to get more flight time,use s for seconds, m for minutes\n" +
                "## and h for hours\n" +
                "#Cooldown: 1h\n" +
                "#\n" +
                "## Type: sqlite for flatfile or mysql for an external database.\n" +
                "#Type: 'sqlite'\n" +
                "## Only edit if 'mysql' is set in Type\n" +
                "#MySQL:\n" +
                "#  # name of your database\n" +
                "#  Database: 'test'\n" +
                "#  # ip of you MySQL database\n" +
                "#  Host: 'localhost'\n" +
                "#  # port of you MySQL database, commonly is 3306\n" +
                "#  Port: 3306\n" +
                "#  # username of you MySQL database\n" +
                "#  Username: 'user'\n" +
                "#  # password of you MySQL database\n" +
                "#  Password: 'root'\n" +
                "#\n" +
                "## Custom command to open the Timed Fly menu, for multiple commands use ';' (example below)\n" +
                "#OpenMenuCommand: 'fly;flymenu'\n" +
                "#\n" +
                "#Gui:\n" +
                "#  # Custom name of the inventory\n" +
                "#  DisplayName: TimedFly\n" +
                "#  # Rows of the inventory (must be multiple of 9, ex: 9, 18... etc)\n" +
                "#  Slots: 9\n" +
                "#\n" +
                "## If true the players time will be saved with the player leaves the server\n" +
                "#StopTimerOnLeave: true\n" +
                "#\n" +
                "## If true the players time will be saved with the player enters one of the blacklisted worlds\n" +
                "## Do not use if you are running the plugin with multiple servers liked via MySQL\n" +
                "#StopTimerOnBlackListedWorld: false\n" +
                "#\n" +
                "## Att: This is still on beta (currently not working)\n" +
                "## If true a bossbar will appear when a player buys time (only servers v1.9 and above)\n" +
                "#BossBarTimer:\n" +
                "#  Enable: false\n" +
                "#  Color: green\n" +
                "#  Style: SEGMENTED_6\n" +
                "#\n" +
                "## If true player will join flying if the have some time left\n" +
                "#JoinFlying:\n" +
                "#  Enabled: true\n" +
                "#  Height: 2\n" +
                "#\n" +
                "## Set to false if you are lazy and you are using spigot 1.8\n" +
                "#Sounds:\n" +
                "#  Enabled: true\n" +
                "#  Announcer: ENTITY_EXPERIENCE_ORB_PICKUP\n" +
                "#  FlightDisabled: ENTITY_WITHER_DEATH\n" +
                "#\n" +
                "## If true the currency used will be from TokenManager, balance placeholder: %tokens%\n" +
                "#UseTokenManager: false\n" +
                "#\n" +
                "## If true the currency used will be from Vault, balance placeholder: %balance%\n" +
                "#UseVault: true\n" +
                "#\n" +
                "## If true players will be charged from their exp levels\n" +
                "#UseLevelsCurrency: false\n" +
                "#\n" +
                "## If true players will be charged from their experience points\n" +
                "#UseExpCurrency: false\n" +
                "#\n" +
                "## If true players with timedfly.fly.onof will enable fly mode when using one of the custom cmds\n" +
                "#FlyModeIfHasPerm: true\n" +
                "#\n" +
                "## If set to true players will need a permission to use the command /fly\n" +
                "#UsePermission:\n" +
                "#  Use: false\n" +
                "#  Permission: 'timedfly.flygui'\n" +
                "#\n" +
                "## This is the limit amount of time a player can add to his fly time (in minutes)\n" +
                "## bypass this with timedfly.limit.bypass.\n" +
                "#LimitMaxTime: 30\n" +
                "#\n" +
                "#Announcer:\n" +
                "#  Chat: false\n" +
                "#  Titles: true\n" +
                "#  Times:\n" +
                "#  - '120'\n" +
                "#  - '60'\n" +
                "#  - '10'\n" +
                "#  - '5'\n" +
                "#  - '4'\n" +
                "#  - '3'\n" +
                "#  - '2'\n" +
                "#  - '1'\n" +
                "#  - '0'\n" +
                "#\n" +
                "## If true it will execute a command when flight disables\n" +
                "#OnFlyDisableCommands:\n" +
                "#  Enabled: false\n" +
                "#  Commands:\n" +
                "#  - '[console] say %player% is using hacks D:'\n" +
                "#  - '[player] say i''m using hacks >:D'\n" +
                "#\n" +
                "## A list of world in which you want the plugin to be enabled.\n" +
                "#World-List:\n" +
                "#  # Available types:\n" +
                "#  # enabled: the plugin will work in worlds listed\n" +
                "#  # disabled: the plugin will not work in worlds listed\n" +
                "#  # all: the plugin will work in all worlds\n" +
                "#  Type: 'all'\n" +
                "#  Worlds:\n" +
                "#  - 'world'\n" +
                "#  - 'survival'\n" +
                "#\n" +
                "## If the server is running aSkyBlock and this is true players flight will be disabled when exiting the island\n" +
                "#ASkyblockIntegration: true\n" +
                "#\n" +
                "## The name says it...\n" +
                "#Check-For-Updates: true\n" +
                "## Auto download new updates\n" +
                "#Auto-Download: false\n" +
                "#\n" +
                "## Choose your own message file (if file name is lang_en.yml use the work after lang_ in this case will be en\n" +
                "## Current languages es, sp, hu, de, you can create your own and send it to me so I added for default.\n" +
                "## Is not recommended to use the /tf reload command, some messages will not change.\n" +
                "#Lang: en\n";
    }
}