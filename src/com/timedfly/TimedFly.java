package com.timedfly;

import com.timedfly.NMS.NMS;
import com.timedfly.NMS.v_1_10.v_1_10_R1;
import com.timedfly.NMS.v_1_11.v_1_11_R1;
import com.timedfly.NMS.v_1_12.v_1_12_R1;
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
import com.timedfly.hooks.TokenManager;
import com.timedfly.listener.*;
import com.timedfly.managers.CurrencyManager;
import com.timedfly.managers.HooksManager;
import com.timedfly.managers.MySQLManager;
import com.timedfly.managers.PlayerManager;
import com.timedfly.updater.Updater;
import com.timedfly.utilities.FlyGUI;
import com.timedfly.utilities.Message;
import com.timedfly.utilities.SqlSetup;
import com.timedfly.utilities.Utilities;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class TimedFly extends JavaPlugin {

    private TokenManager tokensManager;
    private UpdateConfig updateConfig;
    private static MySQLManager sqlManager;
    private ItemsConfig itemsConfig;
    private Languages languages;
    private Utilities utility;
    private SqlSetup sqlSetup;
    private Economy economy;
    private Updater updater;
    private static String version;
    private FlyGUI flyGUI;
    private NMS nms;

    @Override
    public void onEnable() {
        new ConfigCache(this);

        Message.sendConsoleMessage("&7Enabling TimedFly &6v" + this.getDescription().getVersion());
        if (!setupNMS()) {
            Message.sendConsoleMessage("&cUnsupported Minecraft version! It's possible that the plugin fail to work correctly!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        initializeClasses();
        setupCommands();

        if (!sqlSetup.mysqlSetup()) {
            Message.sendConsoleMessage("&cDisabling due to MySQL error!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!setupEconomy()) {
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
            playerManager.setInServer(true).setInitialTime(sqlManager.getInitialTime(player)).setTimeLeft(sqlManager.getTimeLeft(player));
            if (!playerManager.isTimePaused() && utility.isWorldEnabled(player.getWorld()))
                playerManager.startTimedFly();
        });

        updater.sendUpdateMessage();
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());
            sqlManager.saveData(player, playerManager.getTimeLeft(), playerManager.getInitialTime());
        });
        sqlSetup.closeConnection();
    }

    private void initializeClasses() {
        this.utility = new Utilities();
        this.languages = new Languages();
        this.updateConfig = new UpdateConfig(this, languages);
        this.itemsConfig = new ItemsConfig();
        loadConfigs();
        this.sqlSetup = new SqlSetup(this);
        sqlManager = new MySQLManager(sqlSetup);
        this.tokensManager = new TokenManager();
        this.updater = new Updater(this);
        this.flyGUI = new FlyGUI(itemsConfig, languages, this, utility);
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
        CurrencyManager currencyManager = new CurrencyManager(tokensManager, economy, languages);
        Bukkit.getServer().getPluginManager().registerEvents(new FlightTime(nms, flyGUI, utility, languages), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Inventory(currencyManager, utility, languages, itemsConfig, nms), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CustomFlyCMD(utility, languages, flyGUI, nms), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinLeave(utility, sqlManager, this, updater), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChangeWorld(utility, this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new Respawn(utility, this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerOnGround(utility), this);
        if (version.equals("v1_12_R1"))
            Bukkit.getServer().getPluginManager().registerEvents(new AttackMob(this, utility, languages), this);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        Message.sendConsoleMessage("&7Hooking into VaultAPI.");
        return economy != null;
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
        }
        return nms != null;
    }

    public NMS getNMS() {
        return nms;
    }

    public Economy getEconomy() {
        return economy;
    }

    public static MySQLManager getMySqlManager() {
        return sqlManager;
    }

    public static String getVersion() {
        return version;
    }
}
