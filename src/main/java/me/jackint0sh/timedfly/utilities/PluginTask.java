package me.jackint0sh.timedfly.utilities;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PluginTask {

    public static void runAsync(Runnable task) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("TimedFly");
        if (plugin == null) return;
        if (plugin.isEnabled()) Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        else task.run();
    }

    public static void run(Runnable task) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("TimedFly");
        if (plugin == null) return;
        if (plugin.isEnabled()) Bukkit.getScheduler().runTask(plugin, task);
        else task.run();
    }
}
