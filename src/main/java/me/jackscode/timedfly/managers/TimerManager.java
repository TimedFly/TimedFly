package me.jackscode.timedfly.managers;

import me.jackscode.timedfly.api.entity.FlyPlayer;
import me.jackscode.timedfly.api.events.TimedFlyRunningEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class TimerManager {

    private static BukkitTask timer;
    private static final Set<FlyPlayer> players = new HashSet<>();

    private TimerManager() {
    }

    public static synchronized void start() {
        if (!isRunning()) {
            Bukkit.getLogger().info("Starting timer...");
            timer = TaskManager.runAsyncScheduler(() -> {
                if (players.isEmpty()) {
                    Bukkit.getLogger().info("There are no players with timeleft");
                    stop();
                    return;
                }
                players.forEach(tfPlayer -> {
                    if (tfPlayer != null && tfPlayer.hasTime() && tfPlayer.isTimeRunning()) {
                        tfPlayer.decreaseTime();

                        TaskManager.runSync(
                                task -> Bukkit.getPluginManager().callEvent(new TimedFlyRunningEvent(tfPlayer)));

                        if (!tfPlayer.hasTime())
                            TaskManager.runSync(task -> tfPlayer.stopTimer());
                    }
                });
            }, 20, 20);
        }
    }

    public static void stop() {
        if (isRunning()) {
            Bukkit.getLogger().info("Stopping timer...");
            timer.cancel();
        }
    }

    public static boolean isRunning() {
        return timer != null && !timer.isCancelled();
    }

    public static void addPlayer(@NotNull FlyPlayer player) {
        players.add(player);
    }

    public static void removePlayer(@NotNull FlyPlayer player) {
        players.remove(player);
    }
}
