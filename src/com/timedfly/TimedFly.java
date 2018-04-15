package com.timedfly;

import com.timedfly.ConfigurationFiles.ConfigCache;
import com.timedfly.ConfigurationFiles.UpdateConfig;
import com.timedfly.Hooks.Metrics;
import com.timedfly.Hooks.TokensManager;
import com.timedfly.Listeners.GUIListener;
import com.timedfly.Managers.MySQLManager;
import com.timedfly.Managers.RemoveMoney;
import com.timedfly.NMS.NMS;
import com.timedfly.NMS.v_1_10.v_1_10_R1;
import com.timedfly.NMS.v_1_11.v_1_11_R1;
import com.timedfly.NMS.v_1_12.v_1_12_R1;
import com.timedfly.NMS.v_1_8.v_1_8_R1;
import com.timedfly.NMS.v_1_8.v_1_8_R2;
import com.timedfly.NMS.v_1_8.v_1_8_R3;
import com.timedfly.NMS.v_1_9.v_1_9_R1;
import com.timedfly.NMS.v_1_9.v_1_9_R2;
import com.timedfly.Updater.Update;
import com.timedfly.Utilities.PlayerCache;
import com.timedfly.Utilities.Setup;
import com.timedfly.Utilities.Utility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TimedFly extends JavaPlugin {

    private static TimedFly instance;
    private ConsoleCommandSender log = Bukkit.getServer().getConsoleSender();
    private Update updater;

    private Connection connection;
    private Economy economy = null;
    private Setup setup = new Setup();
    private NMS nms;
    private Utility utility;
    private UpdateConfig updateConfig = new UpdateConfig();
    private MySQLManager sqlManager;

    private TokensManager tokensManager;
    private ConfigCache configCache;

    public static TimedFly getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            this.saveDefaultConfig();
        }

        init();
        setupBStats();

        if (utility.isTokenManagerEnabled()) {
            utility.message(log, ("&7Hooked to TokenManager."));
        } else {
            utility.message(log, ("&7TokenManager not found disabling tokens currency."));
        }

        if (setupNMS()) {
            utility.message(log, ("&7Actionbar, Titles, Subtitles support was successfully enabled!"));
        } else {
            utility.message(log, ("&cFailed to setup Actionbar, Titles, Subtitles and Particles support!"));
            utility.message(log, ("&cYour server version is not compatible with this instance!"));
            utility.message(log, ("&cThe instance will not function correctly!"));
        }

        if (!mysqlSetup()) {
            utility.message(log, ("&cDisabled due to MySQL error!"));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        setup.checkForUpdate(this, updater, configCache);
        updateConfig.updateConfig(this);

        Bukkit.getOnlinePlayers().forEach(player -> {
            utility.addPlayerCache(player, sqlManager);
            PlayerCache playerCache = utility.getPlayerCache(player);

            int time = playerCache.getTimeLeft();
            if (!(time <= 0)) GUIListener.flytime.put(player.getUniqueId(), time);

            if (GUIListener.flytime.containsKey(player.getUniqueId())) player.setAllowFlight(true);
        });
        utility.message(log, ("&7The Plugin has been enabled and its ready to use."));
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerCache playerCache = utility.getPlayerCache(player);
            if (GUIListener.flytime.containsKey(player.getUniqueId()) && (utility.isWorldEnabled(player.getWorld()) && player.getAllowFlight())) {
                player.setAllowFlight(false);
                player.setFlying(false);
            }
            sqlManager.setTimeLeft(player, playerCache.getTimeLeft());
            if (player.getOpenInventory().getTitle().equals(ChatColor.translateAlternateColorCodes('&',
                    configCache.getGuiDisplayName()))) player.closeInventory();
        }
        try {
            if (getConnection() != null && !getConnection().isClosed()) getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        utility.message(log, "&7The Plugin has been disabled with success");
    }

    private void init() {
        configCache = new ConfigCache(this);
        configCache.loadConfiguration();

        sqlManager = new MySQLManager(this);
        utility = new Utility(configCache);
        updater = new Update(this);

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("TokenManager")) {
            tokensManager = new TokensManager();
        }

        if (!setupEconomy()) utility.message(log, ("&cNo Vault dependency found! The plugin may not work correctly"));
        else utility.message(log, ("&7Hooked to Vault."));

        RemoveMoney removeMoney = new RemoveMoney(utility, tokensManager, economy, configCache);

        setup.createConfigFiles(this);
        setup.registerCMD(this, utility, configCache);
        setup.registerListener(this, sqlManager, utility, removeMoney, updater, configCache);
        setup.registerDependencies(this);
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
        return economy != null;
    }

    private void setupBStats() {
        Metrics metrics = new Metrics(this);
        metrics.addCustomChart(new Metrics.MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));
    }

    private boolean setupNMS() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return false;
        }

        utility.message(log, "&7Your server is running version " + version);

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

    private boolean mysqlSetup() {
        String host = configCache.getMysqlHost();
        String port = configCache.getMysqlPort();
        String database = configCache.getMysqlDB();
        String username = configCache.getMysqlUser();
        String password = configCache.getMysqlPasss();

        try {
            synchronized (this) {
                if (getConnection() != null && !getConnection().isClosed()) {
                    return false;
                }
                if (configCache.getSqlType().equalsIgnoreCase("sqlite")) {
                    Class.forName("org.sqlite.JDBC");
                    String url = "jdbc:sqlite:" + getDataFolder() + "/SQLite.db";
                    setConnection(DriverManager.getConnection(url));
                    utility.message(log, "&7Successfully connected to &cSQLite &7database.");
                    sqlManager.createTable();
                    return true;
                } else {
                    if (configCache.getSqlType().equalsIgnoreCase("mysql")) {
                        Class.forName("com.mysql.jdbc.Driver");
                        setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", username, password));
                        utility.message(log, "&7Successfully connected to &cMySQL &7database.");
                        sqlManager.createTable();
                        return true;
                    } else {
                        utility.message(log, "&4Could not connect to any database. Please use sqlite or mysql in your MySQL file.");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            utility.message(log, "&cCould not connect to MySQL database, check yor credentials.");
            utility.message(log, e.getMessage());
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void setConnection(Connection connection) {
        this.connection = connection;
    }

    public NMS getNMS() {
        return nms;
    }

    public Economy getEconomy() {
        return economy;
    }

    public Utility getUtility() {
        return utility;
    }
}
