package me.jackint0sh.timedfly;

import me.jackint0sh.timedfly.commands.Arguments;
import me.jackint0sh.timedfly.commands.Main;
import me.jackint0sh.timedfly.commands.TFly;
import me.jackint0sh.timedfly.commands.TabCompleter;
import me.jackint0sh.timedfly.flygui.FlyInventory;
import me.jackint0sh.timedfly.flygui.FlyItem;
import me.jackint0sh.timedfly.hooks.Hooks;
import me.jackint0sh.timedfly.listeners.AttackListener;
import me.jackint0sh.timedfly.listeners.ChatListener;
import me.jackint0sh.timedfly.listeners.InventoryListener;
import me.jackint0sh.timedfly.listeners.PlayerListener;
import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.managers.TimerManager;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class TimedFly extends JavaPlugin {

    public static boolean debug = true;
    private Config itemsConfig;

    @Override
    public void onEnable() {
        MessageUtil.sendConsoleMessage("&cWelcome to TimedFly");
        MessageUtil.sendConsoleMessage("&cLoading assets...");

        this.initializeConfigurations();
        this.registerCommands();
        this.registerEvents();
        this.initializeStoreItems();
        this.initializeTimer();

        MessageUtil.sendConsoleMessage("&cAssets loaded. Plugin ready!");
    }

    @Override
    public void onDisable() {
        MessageUtil.sendConsoleMessage("&cShutting down TimedFly...");

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player -> FlyInventory.inventories.containsKey(player.getOpenInventory().getTitle()))
                .forEach(Player::closeInventory);

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
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

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

        MessageUtil.sendConsoleMessage("&cConfiguration files successfully loaded!!!");
    }

    private void initializeStoreItems() {
        MessageUtil.sendConsoleMessage("&cLoading Store's Items from file...");

        itemsConfig.get().getConfigurationSection("Items").getKeys(false).forEach(FlyItem::new);

        MessageUtil.sendConsoleMessage("&cStore's Items loaded from file...");
    }

    private void initializeHooks() {
        MessageUtil.sendConsoleMessage("&cLooking for hooks...");

        Hooks.hookVault();

        MessageUtil.sendConsoleMessage("&cAll plugins hooked...");
    }
}
