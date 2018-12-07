package com.timedfly;

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
import com.timedfly.listener.*;
import com.timedfly.managers.CurrencyManager;
import com.timedfly.managers.HooksManager;
import com.timedfly.managers.MySQLManager;
import com.timedfly.managers.PlayerManager;
import com.timedfly.updater.Updater;
import com.timedfly.utilities.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public class TimedFly extends JavaPlugin {

    private TokenManager tokensManager;
    private PlayerPointsHook playerPointsHook;
    private UpdateConfig updateConfig;
    private static MySQLManager sqlManager;
    private ItemsConfig itemsConfig;
    private Languages languages;
    private Utilities utility;
    private Vault vault;
    private Updater updater;
    private static String version;
    private FlyGUI flyGUI;
    private NMS nms;
    private SqlProcessor sqlProcessor;

    @Override
    public void onEnable() {
        new ConfigCache(this);

        Message.sendConsoleMessage("&7Enabling TimedFly &6v" + this.getDescription().getVersion());
        if (!setupNMS()) {
            Message.sendConsoleMessage("&cUnsupported Minecraft version! It's possible that the plugin fail to work correctly!");
        }

        try {
            setupSQL();
        } catch (Exception e) {
            Message.sendConsoleMessage("&cDisabling due to MySQL error!");
            throw new IllegalStateException(e);
        }

        initializeClasses();
        setupCommands();

        if (!HooksManager.setupEconomy()) {
            Message.sendConsoleMessage("&cDisabling due to an Economy error!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        HooksManager.setupHooks(this, languages, utility);

        registerListeners();
        Bukkit.getOnlinePlayers().forEach(player -> {
            sqlManager.createPlayer(player);
            utility.addPlayerManager(player.getUniqueId(), this);

            PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());
            playerManager.setInServer(true).setInitialTime(sqlManager.getInitialTime(player))
                    .setTimeLeft(sqlManager.getTimeLeft(player)).setTimeManuallyPaused(sqlManager.getManuallyStopped(player));
            if (!playerManager.isTimePaused() && utility.isWorldEnabled(player.getWorld()) && !playerManager.isTimeManuallyPaused())
                playerManager.startTimedFly();
        });

        updater.sendUpdateMessage();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());
            sqlManager.saveData(player, playerManager.getTimeLeft(), playerManager.getInitialTime(), playerManager.isTimeManuallyPaused());
        });
        try {
            sqlProcessor.release();
        } catch (SQLException e) {
            getLogger().log(Level.WARNING, "SQL close error");
        }
    }

    private void initializeClasses() {
        this.utility = new Utilities();
        this.languages = new Languages();
        this.updateConfig = new UpdateConfig(this, languages);
        this.itemsConfig = new ItemsConfig();
        loadConfigs();
        sqlManager = new MySQLManager(sqlProcessor);
        this.vault = new Vault();
        this.tokensManager = new TokenManager();
        this.playerPointsHook = new PlayerPointsHook();
        this.updater = new Updater(this);
        this.flyGUI = new FlyGUI(itemsConfig, languages, tokensManager, playerPointsHook, utility, vault);
    }

    private void setupSQL() throws Exception {
        this.sqlProcessor = new SqlProcessor(this);
        new Thread(sqlProcessor).start();
    }

    private void setupCommands() {
        this.getCommand("timedfly").setExecutor(new MainCommand(this, languages, itemsConfig, updateConfig));
        this.getCommand("tfly").setExecutor(new FlyCMD(languages, utility, flyGUI, nms));
        this.getCommand("timedfly").setTabCompleter(new MainTabCompletion());
        this.getCommand("tfly").setTabCompleter(new FlyTabCompletion());
    }

    private void loadConfigs() {
        languages.createFiles(this);
        itemsConfig.createFiles(this);
    }

    private void registerListeners() {
        CurrencyManager currencyManager = new CurrencyManager(tokensManager, playerPointsHook, vault, languages);
        Bukkit.getServer().getPluginManager().registerEvents(new FlightTime(nms, flyGUI, utility, languages), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Inventory(currencyManager, utility, languages, itemsConfig, nms), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CustomFlyCMD(utility, languages, flyGUI, nms), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinLeave(utility, sqlManager, this, updater), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChangeWorld(utility, this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChangeGameMode(utility), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Respawn(utility, this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerOnGround(utility), this);

        if (version.startsWith("v1_8")) Bukkit.getServer().getPluginManager().registerEvents(new FallDamage(), this);
        if (version.equals("v1_12_R1"))
            Bukkit.getServer().getPluginManager().registerEvents(new AttackMob(this, utility, languages), this);
    }

    private boolean setupNMS() {
        try {
            version = getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return false;
        }

        Message.sendConsoleMessage("&7Your server is running version &6" + version);

        switch (version) {
            case "v1_8_R1":
                nms = new v_1_8_R1();
                break;
            case "v1_8_R2":
                nms = new v_1_8_R2();
                break;
            case "v1_8_R3":
                nms = new v_1_8_R3();
                break;
            case "v1_9_R1":
                nms = new v_1_9_R1();
                break;
            case "v1_9_R2":
                nms = new v_1_9_R2();
                break;
            case "v1_10_R1":
                nms = new v_1_10_R1();
                break;
            case "v1_11_R1":
                nms = new v_1_11_R1();
                break;
            case "v1_12_R1":
                nms = new v_1_12_R1();
                break;
            case "v1_13_R1":
                nms = new v_1_13_R1();
                break;
            case "v1_13_R2":
                nms = new v_1_13_R2();
                break;
            default:
                nms = new UnSupported();
        }
        return nms.getClass() != UnSupported.class;
    }

    public NMS getNMS() {
        return nms;
    }

    public static MySQLManager getMySqlManager() {
        return sqlManager;
    }

    public static String getVersion() {
        return version;
    }
}
