package com.timedfly.configurations;

import com.timedfly.TimedFly;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigCache {
    private static String prefix;
    private static String cooldown;
    private static String sqlType;
    private static String mysqlDB;
    private static String mysqlHost;
    private static String mysqlPort;
    private static String mysqlUser;
    private static String mysqlPasss;
    private static String openMenuCommand;
    private static String guiDisplayName;
    private static int guiSlots;
    private static boolean guiEnable;
    private static boolean stopTimerOnLeave;
    private static boolean stopTimerOnBlackListedWorld;
    private static boolean bossBarTimerEnabled;
    private static String bossBarTimerColor;
    private static String bossBarTimerStyle;
    private static boolean joinFlyingEnabled;
    private static int joinFlyingHeight;
    private static boolean soundsEnabled;
    private static String soundsAnnouncer;
    private static String soundsFlightDisabled;
    private static boolean useTokenManager;
    private static boolean useVault;
    private static boolean useLevelsCurrency;
    private static boolean useExpCurrency;
    private static boolean flyModeIfHasPerm;
    private static boolean usePermission;
    private static String permission;
    private static int limitMaxTime;
    private static boolean announcerChatEnabled;
    private static boolean announcerTitleEnabled;
    private static List<String> announcerTitleTimes;
    private static boolean messagesTitle;
    private static boolean messagesActionBar;
    private static boolean onFlyDisableCommandsEnabled;
    private static List<String> onFlyDisableCommands;
    private static String worldListType;
    private static List<String> worldListWorlds;
    private static boolean aSkyblockIntegration;
    private static boolean checkForUpdates;
    private static boolean autoDownload;
    private static String language;
    private  FileConfiguration configuration;
    private static boolean disableFlyOnGround;
    private static boolean stopFlyOnAttack;
    private static boolean stopFlyOnGround;
    private static boolean skipFlightTimeIfHasPerm;
    private static boolean logConsoleOnBuy;
    private static boolean debug;

    public ConfigCache(TimedFly plugin) {
        this.configuration = plugin.getConfig();
        plugin.saveDefaultConfig();
        loadConfiguration();
    }

    private void loadConfiguration() {
        ConfigCache.prefix = configuration.getString("Prefix");
        ConfigCache.cooldown = configuration.getString("Cooldown");
        ConfigCache.sqlType = configuration.getString("Type");
        ConfigCache.mysqlDB = configuration.getString("MySQL.Database");
        ConfigCache.mysqlHost = configuration.getString("MySQL.Host");
        ConfigCache.mysqlPort = configuration.getString("MySQL.Port");
        ConfigCache.mysqlUser = configuration.getString("MySQL.Username");
        ConfigCache.mysqlPasss = configuration.getString("MySQL.Password");
        ConfigCache.openMenuCommand = configuration.getString("OpenMenuCommand");
        ConfigCache.guiDisplayName = configuration.getString("Gui.DisplayName");
        ConfigCache.guiSlots = configuration.getInt("Gui.Slots");
        ConfigCache.guiEnable = configuration.getBoolean("Gui.Enable");
        ConfigCache.stopTimerOnLeave = configuration.getBoolean("StopTimerOnLeave");
        ConfigCache.stopTimerOnBlackListedWorld = configuration.getBoolean("StopTimerOnBlackListedWorld");
        ConfigCache.bossBarTimerEnabled = configuration.getBoolean("BossBarTimer.Enabled");
        ConfigCache.bossBarTimerColor = configuration.getString("BossBarTimer.Color");
        ConfigCache.bossBarTimerStyle = configuration.getString("BossBarTimer.Style");
        ConfigCache.joinFlyingEnabled = configuration.getBoolean("JoinFlying.Enabled");
        ConfigCache.joinFlyingHeight = configuration.getInt("JoinFlying.Height");
        ConfigCache.soundsEnabled = configuration.getBoolean("Sounds.Enabled");
        ConfigCache.soundsAnnouncer = configuration.getString("Sounds.Announcer");
        ConfigCache.soundsFlightDisabled = configuration.getString("Sounds.FlightDisabled");
        ConfigCache.useTokenManager = configuration.getBoolean("UseTokenManager");
        ConfigCache.useVault = configuration.getBoolean("UseVault");
        ConfigCache.useLevelsCurrency = configuration.getBoolean("UseLevelsCurrency");
        ConfigCache.useExpCurrency = configuration.getBoolean("UseExpCurrency");
        ConfigCache.flyModeIfHasPerm = configuration.getBoolean("FlyModeIfHasPerm");
        ConfigCache.usePermission = configuration.getBoolean("UsePermission.Use");
        ConfigCache.permission = configuration.getString("UsePermission.Permission");
        ConfigCache.limitMaxTime = configuration.getInt("LimitMaxTime");
        ConfigCache.announcerChatEnabled = configuration.getBoolean("Announcer.Chat");
        ConfigCache.announcerTitleEnabled = configuration.getBoolean("Announcer.Titles");
        ConfigCache.announcerTitleTimes = configuration.getStringList("Announcer.Times");
        ConfigCache.messagesTitle = configuration.getBoolean("Messages.Title");
        ConfigCache.messagesActionBar = configuration.getBoolean("Messages.ActionBar");
        ConfigCache.onFlyDisableCommandsEnabled = configuration.getBoolean("OnFlyDisableCommands.Enabled");
        ConfigCache.onFlyDisableCommands = configuration.getStringList("OnFlyDisableCommands.Commands");
        ConfigCache.disableFlyOnGround = configuration.getBoolean("disableFlyOnGround");
        ConfigCache.worldListType = configuration.getString("World-List.Type");
        ConfigCache.worldListWorlds = configuration.getStringList("World-List.Worlds");
        ConfigCache.aSkyblockIntegration = configuration.getBoolean("ASkyblockIntegration");
        ConfigCache.checkForUpdates = configuration.getBoolean("Check-For-Updates");
        ConfigCache.autoDownload = configuration.getBoolean("Auto-Download");
        ConfigCache.stopFlyOnAttack = configuration.getBoolean("StopFlyOnAttack");
        ConfigCache.stopFlyOnGround = configuration.getBoolean("StopFlyOnGround");
        ConfigCache.skipFlightTimeIfHasPerm = configuration.getBoolean("SkipFlightTimeIfHasPerm");
        ConfigCache.logConsoleOnBuy = configuration.getBoolean("LogConsoleOnBuy");
        ConfigCache.language = configuration.getString("Lang");
        ConfigCache.debug = configuration.getBoolean("Debug");
    }

    public static String getPrefix() {
        return prefix;
    }

    public static String getCooldown() {
        return cooldown;
    }

    public static String getSqlType() {
        return sqlType;
    }

    public static String getMysqlDB() {
        return mysqlDB;
    }

    public static String getMysqlHost() {
        return mysqlHost;
    }

    public static String getMysqlPort() {
        return mysqlPort;
    }

    public static String getMysqlUser() {
        return mysqlUser;
    }

    public static String getMysqlPasss() {
        return mysqlPasss;
    }

    public static String getOpenMenuCommand() {
        return openMenuCommand;
    }

    public static String getGuiDisplayName() {
        return guiDisplayName;
    }

    public static boolean isGuiEnable() {
        return guiEnable;
    }

    public static int getGuiSlots() {
        return guiSlots;
    }

    public static boolean isStopTimerOnLeave() {
        return stopTimerOnLeave;
    }

    public static boolean isStopTimerOnBlackListedWorld() {
        return stopTimerOnBlackListedWorld;
    }

    public static boolean isBossBarTimerEnabled() {
        return bossBarTimerEnabled;
    }

    public static String getBossBarTimerColor() {
        return bossBarTimerColor;
    }

    public static String getBossBarTimerStyle() {
        return bossBarTimerStyle;
    }

    public static boolean isJoinFlyingEnabled() {
        return joinFlyingEnabled;
    }

    public static int getJoinFlyingHeight() {
        return joinFlyingHeight;
    }

    public static boolean isSoundsEnabled() {
        return soundsEnabled;
    }

    public static String getSoundsAnnouncer() {
        return soundsAnnouncer;
    }

    public static String getSoundsFlightDisabled() {
        return soundsFlightDisabled;
    }

    public static boolean isUseTokenManager() {
        return useTokenManager;
    }

    public static boolean isUseVault() {
        return useVault;
    }

    public static boolean isUseLevelsCurrency() {
        return useLevelsCurrency;
    }

    public static boolean isUseExpCurrency() {
        return useExpCurrency;
    }

    public static boolean isFlyModeIfHasPerm() {
        return flyModeIfHasPerm;
    }

    public static boolean isUsePermission() {
        return usePermission;
    }

    public static String getPermission() {
        return permission;
    }

    public static int getLimitMaxTime() {
        return limitMaxTime;
    }

    public static boolean isAnnouncerChatEnabled() {
        return announcerChatEnabled;
    }

    public static boolean isAnnouncerTitleEnabled() {
        return announcerTitleEnabled;
    }

    public static List<String> getAnnouncerTitleTimes() {
        return announcerTitleTimes;
    }

    public static boolean isMessagesTitle() {
        return messagesTitle;
    }

    public static boolean isMessagesActionBar() {
        return messagesActionBar;
    }

    public static boolean isOnFlyDisableCommandsEnabled() {
        return onFlyDisableCommandsEnabled;
    }

    public static List<String> getOnFlyDisableCommands() {
        return onFlyDisableCommands;
    }

    public static String getWorldListType() {
        return worldListType;
    }

    public static List<String> getWorldListWorlds() {
        return worldListWorlds;
    }

    public static boolean isaSkyblockIntegration() {
        return aSkyblockIntegration;
    }

    public static boolean isCheckForUpdates() {
        return checkForUpdates;
    }

    public static boolean isAutoDownload() {
        return autoDownload;
    }

    public static String getLanguage() {
        return language;
    }

    public static boolean isDisableFlyOnGround() {
        return disableFlyOnGround;
    }

    public static boolean isStopFlyOnAttack() {
        return stopFlyOnAttack;
    }

    public static boolean isStopFlyOnGround() {
        return stopFlyOnGround;
    }

    public static boolean isSkipFlightTimeIfHasPerm() {
        return skipFlightTimeIfHasPerm;
    }

    public static boolean isLogConsoleOnBuy() {
        return logConsoleOnBuy;
    }

    public static boolean isDebug() {
        return debug;
    }
}
