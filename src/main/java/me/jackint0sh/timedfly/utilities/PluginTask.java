package me.jackint0sh.timedfly.utilities;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

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

    public static BukkitTask runLater(Runnable task, long delay) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("TimedFly");
        if (plugin == null || !plugin.isEnabled()) return null;
        return Bukkit.getScheduler().runTaskLater(plugin, task, delay);
    }
}
