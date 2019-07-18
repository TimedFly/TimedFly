package me.jackint0sh.timedfly.managers;

import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class TimerManager {

    private static BukkitTask timer;

    public static void startIfNot() {
        if (timer == null || timer.isCancelled()) start();
    }

    public static void start() {
        MessageUtil.sendConsoleMessage("&cInitializing fly timer...");

        if (timer != null && !timer.isCancelled()) {
            MessageUtil.sendError("Timer already initialized!!!");
            return;
        }

        timer = new Timer().runTaskTimer(Bukkit.getPluginManager().getPlugins()[0], 0, 0);
        MessageUtil.sendConsoleMessage("&cFly timer initialized!");
    }

    private static void stop() {
        MessageUtil.sendConsoleMessage("&cStopping fly timer...");
        if (timer != null && !timer.isCancelled()) {
            timer.cancel();
            timer = null;
            MessageUtil.sendConsoleMessage("&cFly timer stopped!");
        } else MessageUtil.sendError("Fly timer already stopped!!!");

    }

    private static class Timer extends BukkitRunnable {
        @Override
        public void run() {
            if (PlayerManager.getPlayersTimeLeft() < 0) {
                MessageUtil.sendConsoleMessage("&cThere are no players with a timer running.");
                TimerManager.stop();
            }

            for (UUID uuid : PlayerManager.getPlayerCache().keySet()) {
                PlayerManager playerManager = PlayerManager.getCachedPlayer(uuid);

                if (!playerManager.isTimeRunning()) continue;

                if (playerManager.getTimeLeft() <= 0) playerManager.stopTimer();
                playerManager.decreaseTime().updateStore();
            }
        }
    }
}
