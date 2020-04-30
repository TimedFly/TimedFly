package me.jackscode.timedfly.managers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerManager {

    private final static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static boolean isStopped;

    public static void start() {
        if (isStopped) {
            scheduler.scheduleAtFixedRate(() -> {
                // TODO: Loop through players and do things
                // Bukkit.getPluginManager().callEvent(new TimedFlyEndEvent(this));
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

    public static void stop() throws InterruptedException {
        if (!isStopped) {
            scheduler.shutdownNow();
            scheduler.awaitTermination(1, TimeUnit.SECONDS);
            isStopped = true;
        }
    }

}
