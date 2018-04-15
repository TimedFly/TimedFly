package com.timedfly.Utilities;

import com.timedfly.CMDs.Completitions.FlyTabCompletion;
import com.timedfly.CMDs.Completitions.MainTabCompletion;
import com.timedfly.CMDs.CustomFlyCMD;
import com.timedfly.CMDs.FlyCMD;
import com.timedfly.CMDs.MainCMD;
import com.timedfly.ConfigurationFiles.ConfigCache;
import com.timedfly.ConfigurationFiles.ItemsConfig;
import com.timedfly.ConfigurationFiles.LangFiles;
import com.timedfly.Hooks.PlaceholderAPI;
import com.timedfly.Hooks.aSkyblock;
import com.timedfly.Listeners.GUIListener;
import com.timedfly.Listeners.GeneralListener;
import com.timedfly.Managers.MySQLManager;
import com.timedfly.Managers.RemoveMoney;
import com.timedfly.TimedFly;
import com.timedfly.Updater.Update;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public class Setup {

    public void checkForUpdate(TimedFly plugin, Update updater, ConfigCache configCache) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (configCache.isCheckForUpdates()) {
                    updater.sendUpdateMessage();
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if (player.hasPermission("timedfly.getupdate")) updater.sendUpdateMessage(player);
                    });
                }
            }
        }.runTaskTimer(plugin, 20L, 15 * 34500L);
    }

    public void registerCMD(TimedFly plugin, Utility utility, ConfigCache configCache) {
        plugin.getCommand("timedfly").setExecutor(new MainCMD(plugin, configCache));
        plugin.getCommand("tfly").setExecutor(new FlyCMD(plugin, utility, configCache));
        plugin.getCommand("timedfly").setTabCompleter(new MainTabCompletion());
        plugin.getCommand("tfly").setTabCompleter(new FlyTabCompletion());
    }

    public void registerListener(TimedFly plugin, MySQLManager sqlManager, Utility utility, RemoveMoney removeMoney, Update updater, ConfigCache configCache) {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new GUIListener(plugin, utility, removeMoney, configCache), plugin);
        pm.registerEvents(new GeneralListener(plugin, sqlManager, utility, updater, configCache), plugin);
        pm.registerEvents(new CustomFlyCMD(plugin, configCache), plugin);

        if (Bukkit.getPluginManager().isPluginEnabled("ASkyBlock") && configCache.isaSkyblockIntegration()) {
            pm.registerEvents(new aSkyblock(), plugin);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&cTimedFly >> &7ASkyBlock found, enabling the hook."));
        }
    }

    public void createConfigFiles(TimedFly plugin) {
        LangFiles lang = LangFiles.getInstance();
        ItemsConfig items = ItemsConfig.getInstance();

        lang.createFiles(plugin);
        items.createFiles(plugin);
    }

    public void registerDependencies(TimedFly plugin) {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPI(plugin).hook();
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&cTimedFly >> &7PlaceholderAPI found, using it for item's lore and name."));
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&cTimedFly >> &7PlaceholderAPI not found, you should install it"));
        }
    }

}
