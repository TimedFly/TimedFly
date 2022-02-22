package me.jackint0sh.timedfly;

import me.jackint0sh.timedfly.commands.*;
import me.jackint0sh.timedfly.database.DatabaseHandler;
import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.FlyItem;
import me.jackint0sh.timedfly.hooks.Hooks;
import me.jackint0sh.timedfly.listeners.*;
import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.managers.TimerManager;
import me.jackint0sh.timedfly.managers.UpdateManager;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.Languages;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.versions.Default;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.MultiLineChart;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public final class TimedFly extends JavaPlugin {

    public static boolean debug = true;
    private Config itemsConfig;
    private UpdateManager updateManager;

    @Override
    public void onEnable() {
        updateManager = new UpdateManager(48668, this.getDescription().getVersion());

        MessageUtil.sendConsoleMessage("&cWelcome to TimedFly");
        MessageUtil.sendConsoleMessage("&cLoading assets...");

        if (!this.initializeSupportedVersion()) return;
        this.initializeConfigurations();
        if (!DatabaseHandler.initialize()) return;
        this.initializeLanguages();
        this.initializeHooks();
        this.registerCommands();
        this.registerEvents();
        this.initializeStoreItems();
        this.initializeTimer();

        MessageUtil.setPluginName();

        Metrics metrics = new Metrics(this, 48668);
        metrics.addCustomChart(new MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));

        Bukkit.getOnlinePlayers().forEach(player ->
            PlayerListener.handlePlayerQuery(PlayerManager.getCachedPlayer(player.getUniqueId()), false)
        );

        MessageUtil.sendConsoleMessage("&cAssets loaded. Plugin ready!");

        try {
            updateManager.checkForUpdate(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        MessageUtil.sendConsoleMessage("&cShutting down TimedFly...");

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (FlyInventory.inventories.containsKey(player.getOpenInventory().getTitle())) {
                player.closeInventory();
            }
            PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId()).setTimeRunning(false);
            PlayerListener.handlePlayerQuery(playerManager, true);
        });

        DatabaseHandler.close();
        MessageUtil.sendConsoleMessage("&cTimedFly disabled!");
    }

    private void registerCommands() {
        MessageUtil.sendConsoleMessage("&cLoading commands...");

        Bukkit.getPluginCommand("timedfly").setExecutor(new Main());
        Bukkit.getPluginCommand("tfly").setExecutor(new TFly());

        Bukkit.getPluginCommand("timedfly").setTabCompleter(new TabCompleter(Arguments.Type.TIMEDFLY));
        Bukkit.getPluginCommand("tfly").setTabCompleter(new TabCompleter(Arguments.Type.TFLY));

        MessageUtil.sendConsoleMessage("&cCommands successfully loaded!");
    }

    private void registerEvents() {
        MessageUtil.sendConsoleMessage("&cLoading event listeners...");

        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new AttackListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(updateManager), this);
        Bukkit.getPluginManager().registerEvents(new TimedFlyListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomCommand(), this);

        MessageUtil.sendConsoleMessage("&cEvent listeners successfully loaded!");
    }

    private void initializeTimer() {
        if (PlayerManager.getPlayerCache().size() > 0) {
            TimerManager.start();
        }
    }

    private void initializeConfigurations() {
        MessageUtil.sendConsoleMessage("&cLoading configuration files...");

        try {
            itemsConfig = new Config("items", this).create();
            new Config("config", this).create();
        } catch (IOException | InvalidConfigurationException e) {
            MessageUtil.sendError(e.getMessage());
            if (TimedFly.debug) e.printStackTrace();
            return;
        }

        MessageUtil.sendConsoleMessage("&cConfiguration files successfully loaded!");
    }

    private void initializeStoreItems() {
        MessageUtil.sendConsoleMessage("&cLoading Store's Items from file...");

        itemsConfig.get().getConfigurationSection("Items").getKeys(false).forEach(FlyItem::new);

        MessageUtil.sendConsoleMessage("&cStore's Items loaded from file!");
    }

    private void initializeHooks() {
        MessageUtil.sendConsoleMessage("&cLooking for hooks...");

        Hooks.hookVault();
        Hooks.hookPlayerPoints();
        Hooks.hookTokenManager();
        Hooks.registerOtherCurrencies();
        Hooks.hookPapi(this);

        MessageUtil.sendConsoleMessage("&cAll plugins hooked!");
    }

    private boolean initializeSupportedVersion() {
        String version;
        try {
            version = getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            MessageUtil.sendError(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        MessageUtil.sendConsoleMessage("Your server is running version &6" + version);

        try {
            Class<?> clazz = Class.forName("me.jackint0sh.timedfly.versions." + version.substring(0, version.length() - 3) + "." + version);
            Constructor<?> ctor = clazz.getConstructor();
            ctor.newInstance();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            MessageUtil.sendError("Something went wrong while trying to enable NMS for &6" + version);
            MessageUtil.sendError("Using default class.");
            new Default();
        }

        return true;
    }

    private void initializeLanguages() {
        MessageUtil.sendConsoleMessage("Loading languages...");
        Languages.createFiles();
        Languages.loadLang();
        MessageUtil.sendConsoleMessage("Languages loaded!");
    }
}
