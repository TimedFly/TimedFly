package com.timedfly.ConfigurationFiles;

import com.timedfly.TimedFly;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigCache {

    private String prefix;
    private String cooldown;
    private String sqlType;
    private String mysqlDB;
    private String mysqlHost;
    private String mysqlPort;
    private String mysqlUser;
    private String mysqlPasss;
    private String openMenuCommand;
    private String guiDisplayName;
    private int guiSlots;
    private boolean stopTimerOnLeave;
    private boolean stopTimerOnBlackListedWorld;
    private boolean bossBarTimerEnabled;
    private String bossBarTimerColor;
    private String bossBarTimerStyle;
    private boolean joinFlyingEnabled;
    private int joinFlyingHeight;
    private boolean soundsEnabled;
    private String soundsAnnouncer;
    private String soundsFlightDisabled;
    private boolean useTokenManager;
    private boolean useVault;
    private boolean useLevelsCurrency;
    private boolean useExpCurrency;
    private boolean flyModeIfHasPerm;
    private boolean usePermission;
    private String permission;
    private int limitMaxTime;
    private boolean announcerChatEnabled;
    private boolean announcerTitleEnabled;
    private List<String> announcerTitleTimes;
    private boolean messagesTitle;
    private boolean messagesActionBar;
    private boolean onFlyDisableCommandsEnabled;
    private List<String> onFlyDisableCommands;
    private String worldListType;
    private List<String> worldListWorlds;
    private boolean aSkyblockIntegration;
    private boolean checkForUpdates;
    private boolean autoDownload;
    private String languge;
    private FileConfiguration configuration;
    private boolean disableFlyOnGround;
    private boolean stopFlyOnAttack;

    public ConfigCache(TimedFly plugin) {
        this.configuration = plugin.getConfig();
    }

    public void loadConfiguration() {
        prefix = configuration.getString("Prefix");
        cooldown = configuration.getString("Cooldown");
        sqlType = configuration.getString("Type");
        mysqlDB = configuration.getString("MySQL.Database");
        mysqlHost = configuration.getString("MySQL.Host");
        mysqlPort = configuration.getString("MySQL.Port");
        mysqlUser = configuration.getString("MySQL.Username");
        mysqlPasss = configuration.getString("MySQL.Password");
        openMenuCommand = configuration.getString("OpenMenuCommand");
        guiDisplayName = configuration.getString("Gui.DisplayName");
        guiSlots = configuration.getInt("Gui.Slots");
        stopTimerOnLeave = configuration.getBoolean("StopTimerOnLeave");
        stopTimerOnBlackListedWorld = configuration.getBoolean("StopTimerOnBlackListedWorld");
        bossBarTimerEnabled = configuration.getBoolean("BossBarTimer.Enabled");
        bossBarTimerColor = configuration.getString("BossBarTimer.Color");
        bossBarTimerStyle = configuration.getString("BossBarTimer.Style");
        joinFlyingEnabled = configuration.getBoolean("JoinFlying.Enabled");
        joinFlyingHeight = configuration.getInt("JoinFlying.Height");
        soundsEnabled = configuration.getBoolean("Sounds.Enabled");
        soundsAnnouncer = configuration.getString("Sounds.Announcer");
        soundsFlightDisabled = configuration.getString("Sounds.FlightDisabled");
        useTokenManager = configuration.getBoolean("UseTokenManager");
        useVault = configuration.getBoolean("UseVault");
        useLevelsCurrency = configuration.getBoolean("UseLevelsCurrency");
        useExpCurrency = configuration.getBoolean("UseExpCurrency");
        flyModeIfHasPerm = configuration.getBoolean("FlyModeIfHasPerm");
        usePermission = configuration.getBoolean("UsePermission.Use");
        permission = configuration.getString("UsePermission.Permission");
        limitMaxTime = configuration.getInt("LimitMaxTime");
        announcerChatEnabled = configuration.getBoolean("Announcer.Chat");
        announcerTitleEnabled = configuration.getBoolean("Announcer.Titles");
        announcerTitleTimes = configuration.getStringList("Announcer.Times");
        messagesTitle = configuration.getBoolean("Messages.Title");
        messagesActionBar = configuration.getBoolean("Messages.ActionBar");
        onFlyDisableCommandsEnabled = configuration.getBoolean("OnFlyDisableCommands.Enabled");
        onFlyDisableCommands = configuration.getStringList("OnFlyDisableCommands.Commands");
        disableFlyOnGround = configuration.getBoolean("disableFlyOnGround");
        worldListType = configuration.getString("World-List.Type");
        worldListWorlds = configuration.getStringList("World-List.Worlds");
        aSkyblockIntegration = configuration.getBoolean("ASkyblockIntegration");
        checkForUpdates = configuration.getBoolean("Check-For-Updates");
        autoDownload = configuration.getBoolean("Auto-Download");
        stopFlyOnAttack = configuration.getBoolean("StopFlyOnAttack");
        languge = configuration.getString("Lang");
    }

    public String getPrefix() {
        return prefix;
    }

    public String getCooldown() {
        return cooldown;
    }

    public String getSqlType() {
        return sqlType;
    }

    public String getMysqlDB() {
        return mysqlDB;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public String getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlUser() {
        return mysqlUser;
    }

    public String getMysqlPasss() {
        return mysqlPasss;
    }

    public String getOpenMenuCommand() {
        return openMenuCommand;
    }

    public String getGuiDisplayName() {
        return guiDisplayName;
    }

    public int getGuiSlots() {
        return guiSlots;
    }

    public boolean isStopTimerOnLeave() {
        return stopTimerOnLeave;
    }

    public boolean isStopTimerOnBlackListedWorld() {
        return stopTimerOnBlackListedWorld;
    }

    public boolean isBossBarTimerEnabled() {
        return bossBarTimerEnabled;
    }

    public String getBossBarTimerColor() {
        return bossBarTimerColor;
    }

    public String getBossBarTimerStyle() {
        return bossBarTimerStyle;
    }

    public boolean isJoinFlyingEnabled() {
        return joinFlyingEnabled;
    }

    public int getJoinFlyingHeight() {
        return joinFlyingHeight;
    }

    public boolean isSoundsEnabled() {
        return soundsEnabled;
    }

    public String getSoundsAnnouncer() {
        return soundsAnnouncer;
    }

    public String getSoundsFlightDisabled() {
        return soundsFlightDisabled;
    }

    public boolean isUseTokenManager() {
        return useTokenManager;
    }

    public boolean isUseVault() {
        return useVault;
    }

    public boolean isUseLevelsCurrency() {
        return useLevelsCurrency;
    }

    public boolean isUseExpCurrency() {
        return useExpCurrency;
    }

    public boolean isFlyModeIfHasPerm() {
        return flyModeIfHasPerm;
    }

    public boolean isUsePermission() {
        return usePermission;
    }

    public String getPermission() {
        return permission;
    }

    public int getLimitMaxTime() {
        return limitMaxTime;
    }

    public boolean isAnnouncerChatEnabled() {
        return announcerChatEnabled;
    }

    public boolean isAnnouncerTitleEnabled() {
        return announcerTitleEnabled;
    }

    public List<String> getAnnouncerTitleTimes() {
        return announcerTitleTimes;
    }

    public boolean isMessagesTitle() {
        return messagesTitle;
    }

    public boolean isMessagesActionBar() {
        return messagesActionBar;
    }

    public boolean isOnFlyDisableCommandsEnabled() {
        return onFlyDisableCommandsEnabled;
    }

    public List<String> getOnFlyDisableCommands() {
        return onFlyDisableCommands;
    }

    public String getWorldListType() {
        return worldListType;
    }

    public List<String> getWorldListWorlds() {
        return worldListWorlds;
    }

    public boolean isaSkyblockIntegration() {
        return aSkyblockIntegration;
    }

    public boolean isCheckForUpdates() {
        return checkForUpdates;
    }

    public boolean isAutoDownload() {
        return autoDownload;
    }

    public String getLanguge() {
        return languge;
    }

    public boolean isDisableFlyOnGround() {
        return disableFlyOnGround;
    }

    public boolean isStopFlyOnAttack() {
        return stopFlyOnAttack;
    }
}
