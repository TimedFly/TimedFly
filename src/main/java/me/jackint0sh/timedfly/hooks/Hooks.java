package me.jackint0sh.timedfly.hooks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class Hooks {

    public static boolean isPluginEnabled(String pluginName) {
        PluginManager manager = Bukkit.getPluginManager();
        return manager.isPluginEnabled(pluginName)&& manager.getPlugin(pluginName) != null;
    }

    public static Plugin getPlugin(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName);
    }


}
