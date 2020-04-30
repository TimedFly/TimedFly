package me.jackscode.timedfly.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class TaskManager {

    private static final Plugin plugin = Bukkit.getPluginManager().getPlugin("TimedFly");

    public static void runAsync(Consumer<BukkitTask> callback) {
        if (plugin == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, callback);
    }

    public static void runSync(Consumer<BukkitTask> callback) {
        if (plugin == null) return;
        Bukkit.getScheduler().runTask(plugin, callback);
    }

    public static void runAsyncButSync(Consumer<BukkitTask> callback) {
        TaskManager.runAsync(task -> TaskManager.runSync(callback));
    }

}
