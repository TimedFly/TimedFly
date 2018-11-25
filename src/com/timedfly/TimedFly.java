package com.timedfly;

import com.github.entrypointkr.timedfly.SqlProcessor;
import com.timedfly.NMS.NMS;
import com.timedfly.NMS.UnSupported;
import com.timedfly.NMS.v_1_10.v_1_10_R1;
import com.timedfly.NMS.v_1_11.v_1_11_R1;
import com.timedfly.NMS.v_1_12.v_1_12_R1;
import com.timedfly.NMS.v_1_13.v_1_13_R1;
import com.timedfly.NMS.v_1_13.v_1_13_R2;
import com.timedfly.NMS.v_1_8.v_1_8_R1;
import com.timedfly.NMS.v_1_8.v_1_8_R2;
import com.timedfly.NMS.v_1_8.v_1_8_R3;
import com.timedfly.NMS.v_1_9.v_1_9_R1;
import com.timedfly.NMS.v_1_9.v_1_9_R2;
import com.timedfly.commands.Completitions.FlyTabCompletion;
import com.timedfly.commands.Completitions.MainTabCompletion;
import com.timedfly.commands.CustomFlyCMD;
import com.timedfly.commands.FlyCMD;
import com.timedfly.commands.MainCommand;
import com.timedfly.configurations.ConfigCache;
import com.timedfly.configurations.ItemsConfig;
import com.timedfly.configurations.Languages;
import com.timedfly.configurations.UpdateConfig;
import com.timedfly.hooks.PlayerPointsHook;
import com.timedfly.hooks.TokenManager;
import com.timedfly.hooks.Vault;
import com.timedfly.listener.AttackMob;
import com.timedfly.listener.ChangeGameMode;
import com.timedfly.listener.ChangeWorld;
import com.timedfly.listener.FallDamage;
import com.timedfly.listener.FlightTime;
import com.timedfly.listener.Inventory;
import com.timedfly.listener.JoinLeave;
import com.timedfly.listener.PlayerOnGround;
import com.timedfly.listener.Respawn;
import com.timedfly.managers.CurrencyManager;
import com.timedfly.managers.HooksManager;
import com.timedfly.managers.MySQLManager;
import com.timedfly.managers.PlayerManager;
import com.timedfly.updater.Updater;
import com.timedfly.utilities.FlyGUI;
import com.timedfly.utilities.Message;
import com.timedfly.utilities.SqlSetup;
import com.timedfly.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class TimedFly extends JavaPlugin {
    private TokenManager tokensManager;
    private PlayerPointsHook playerPointsHook;
    private UpdateConfig updateConfig;
    private static MySQLManager sqlManager;
    private ItemsConfig itemsConfig;
    private Languages languages;
    private Utilities utility;
    private SqlSetup sqlSetup;
    private Vault vault;
    private Updater updater;
    private static String version;
    private FlyGUI flyGUI;
    private NMS nms;
    private final SqlProcessor sqlProcessor;

    public TimedFly() {
        this.sqlProcessor = new SqlProcessor(this);
    }

    public void onEnable() {
        new Thread(sqlProcessor).start();
        new ConfigCache(this);
        Message.sendConsoleMessage("&7Enabling TimedFly &6v" + this.getDescription().getVersion());
        if (!this.setupNMS()) {
            Message.sendConsoleMessage("&cUnsupported Minecraft version! It's possible that the plugin fail to work correctly!");
        }

        this.initializeClasses();
        this.setupCommands();
        if (!this.sqlSetup.mysqlSetup()) {
            Message.sendConsoleMessage("&cDisabling due to MySQL error!");
            this.getServer().getPluginManager().disablePlugin(this);
        } else if (!HooksManager.setupEconomy()) {
            Message.sendConsoleMessage("&cDisabling due to an Economy error!");
            this.getServer().getPluginManager().disablePlugin(this);
        } else {
            HooksManager.setupHooks(this, this.languages, this.utility);
            this.registerListeners();
            Bukkit.getOnlinePlayers().forEach((player) -> {
                sqlManager.createPlayer(player);
                this.utility.addPlayerManager(player.getUniqueId(), this);
                PlayerManager playerManager = this.utility.getPlayerManager(player.getUniqueId());
                playerManager.setInServer(true).setInitialTime(sqlManager.getInitialTime(player)).setTimeLeft(sqlManager.getTimeLeft(player)).setTimeManuallyPaused(sqlManager.getManuallyStopped(player));
                if (!playerManager.isTimePaused() && this.utility.isWorldEnabled(player.getWorld()) && !playerManager.isTimeManuallyPaused()) {
                    playerManager.startTimedFly();
                }

            });
            this.updater.sendUpdateMessage();
        }
    }

    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach((player) -> {
            PlayerManager playerManager = this.utility.getPlayerManager(player.getUniqueId());
            sqlManager.saveData(player, playerManager.getTimeLeft(), playerManager.getInitialTime(), playerManager.isTimeManuallyPaused());
        });
        this.sqlSetup.closeConnection();
        try {
            sqlProcessor.release();
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void initializeClasses() {
        this.utility = new Utilities();
        this.languages = new Languages();
        this.updateConfig = new UpdateConfig(this, this.languages);
        this.itemsConfig = new ItemsConfig();
        this.loadConfigs();
        this.sqlSetup = new SqlSetup(this);
        sqlManager = new MySQLManager(this.sqlSetup);
        this.vault = new Vault();
        this.tokensManager = new TokenManager();
        this.playerPointsHook = new PlayerPointsHook();
        this.updater = new Updater(this);
        this.flyGUI = new FlyGUI(this.itemsConfig, this.languages, this.tokensManager, this.playerPointsHook, this.utility, this.vault);
    }

    private void setupCommands() {
        this.getCommand("timedfly").setExecutor(new MainCommand(this, this.languages, this.itemsConfig, this.updateConfig));
        this.getCommand("tfly").setExecutor(new FlyCMD(this.languages, this.utility, this.flyGUI, this.nms));
        this.getCommand("timedfly").setTabCompleter(new MainTabCompletion());
        this.getCommand("tfly").setTabCompleter(new FlyTabCompletion());
    }

    private void loadConfigs() {
        this.languages.createFiles(this);
        this.itemsConfig.createFiles(this);
    }

    private void registerListeners() {
        CurrencyManager currencyManager = new CurrencyManager(this.tokensManager, this.playerPointsHook, this.vault, this.languages);
        Bukkit.getServer().getPluginManager().registerEvents(new FlightTime(this.nms, this.flyGUI, this.utility, this.languages), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Inventory(currencyManager, this.utility, this.languages, this.itemsConfig, this.nms), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CustomFlyCMD(this.utility, this.languages, this.flyGUI, this.nms), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinLeave(this.utility, sqlManager, this, this.updater), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChangeWorld(this.utility, this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChangeGameMode(this.utility), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Respawn(this.utility, this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerOnGround(this.utility), this);
        if (version.startsWith("v1_8")) {
            Bukkit.getServer().getPluginManager().registerEvents(new FallDamage(), this);
        }

        if (version.equals("v1_12_R1")) {
            Bukkit.getServer().getPluginManager().registerEvents(new AttackMob(this, this.utility, this.languages), this);
        }

    }

    private boolean setupNMS() {
        try {
            version = this.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException var3) {
            return false;
        }

        Message.sendConsoleMessage("&7Your server is running version &6" + version);
        String var1 = version;
        byte var2 = -1;
        switch (var1.hashCode()) {
            case -1497224837:
                if (var1.equals("v1_10_R1")) {
                    var2 = 5;
                }
                break;
            case -1497195046:
                if (var1.equals("v1_11_R1")) {
                    var2 = 6;
                }
                break;
            case -1497165255:
                if (var1.equals("v1_12_R1")) {
                    var2 = 7;
                }
                break;
            case -1497135464:
                if (var1.equals("v1_13_R1")) {
                    var2 = 8;
                }
                break;
            case -1497135463:
                if (var1.equals("v1_13_R2")) {
                    var2 = 9;
                }
                break;
            case -1156422966:
                if (var1.equals("v1_8_R1")) {
                    var2 = 0;
                }
                break;
            case -1156422965:
                if (var1.equals("v1_8_R2")) {
                    var2 = 1;
                }
                break;
            case -1156422964:
                if (var1.equals("v1_8_R3")) {
                    var2 = 2;
                }
                break;
            case -1156393175:
                if (var1.equals("v1_9_R1")) {
                    var2 = 3;
                }
                break;
            case -1156393174:
                if (var1.equals("v1_9_R2")) {
                    var2 = 4;
                }
        }

        switch (var2) {
            case 0:
                this.nms = new v_1_8_R1();
                break;
            case 1:
                this.nms = new v_1_8_R2();
                break;
            case 2:
                this.nms = new v_1_8_R3();
                break;
            case 3:
                this.nms = new v_1_9_R1();
                break;
            case 4:
                this.nms = new v_1_9_R2();
                break;
            case 5:
                this.nms = new v_1_10_R1();
                break;
            case 6:
                this.nms = new v_1_11_R1();
                break;
            case 7:
                this.nms = new v_1_12_R1();
                break;
            case 8:
                this.nms = new v_1_13_R1();
                break;
            case 9:
                this.nms = new v_1_13_R2();
                break;
            default:
                this.nms = new UnSupported();
        }

        return this.nms.getClass() != UnSupported.class;
    }

    public NMS getNMS() {
        return this.nms;
    }

    public static MySQLManager getMySqlManager() {
        return sqlManager;
    }

    public static String getVersion() {
        return version;
    }

    public SqlProcessor getSqlProcessor() {
        return sqlProcessor;
    }
}
