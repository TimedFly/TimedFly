package me.jackint0sh.timedfly.managers;

import me.jackint0sh.timedfly.events.TimedFlyRunningEvent;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class TimerManager {

    private static BukkitTask timer;
    private static boolean cancelled;

    public static void startIfNot() {
        if (timer == null || cancelled) start();
    }

    public static void start() {
        MessageUtil.sendConsoleMessage("&cInitializing fly timer...");

        if (timer != null && !cancelled) {
            MessageUtil.sendError("Timer already initialized!!!");
            return;
        }

        timer = new Timer().runTaskTimer(Bukkit.getPluginManager().getPlugins()[0], 20, 20);
        MessageUtil.sendConsoleMessage("&cFly timer initialized!");
    }

    private static void stop() {
        cancelled = true;
        if (timer != null) {
            timer.cancel();
            timer = null;
            MessageUtil.sendConsoleMessage("&cFly timer stopped!");
        }
    }

    private static class Timer extends BukkitRunnable {
        @Override
        public void run() {
            for (UUID uuid : PlayerManager.getPlayerCache().keySet()) {
                PlayerManager playerManager = PlayerManager.getCachedPlayer(uuid);

                if (!playerManager.isTimeRunning()) continue;

                if (!playerManager.hasTime()) playerManager.stopTimer();
                playerManager.decreaseTime().updateStore();
                Bukkit.getPluginManager().callEvent(new TimedFlyRunningEvent(playerManager));
            }

            if (Config.getConfig("config").get().getBoolean("AutoStopTimer") && PlayerManager.getPlayersTimeLeft() < 1) {
                MessageUtil.sendConsoleMessage("&cThere are no players with a timer running.");
                TimerManager.stop();
            }
        }
    }
}
