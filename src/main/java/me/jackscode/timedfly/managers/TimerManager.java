package me.jackscode.timedfly.managers;

import me.jackscode.timedfly.api.entity.TFPlayer;
import me.jackscode.timedfly.api.events.TimedFlyRunningEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.IdentityHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimerManager {

    private final ScheduledExecutorService scheduler;
    private final IdentityHashMap<Player, TFPlayer> players;
    private ScheduledFuture<?> scheduledFuture;

    {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.players = new IdentityHashMap<>();
    }

    public void start() {
        if (!isRunning()) {
            System.out.println("Starting timer...");
            this.scheduledFuture = this.scheduler.scheduleAtFixedRate(() -> {
                if (this.players.isEmpty()) return;

                this.players.forEach((player, tfPlayer) -> {
                    if (tfPlayer == null || !tfPlayer.hasTime() || !tfPlayer.isTimeRunning()) return;
                    tfPlayer.decreaseTime();

                    TaskManager.runSync((task) -> {
                        Bukkit.getPluginManager().callEvent(new TimedFlyRunningEvent(tfPlayer));
                    });

                    if (!tfPlayer.hasTime()) {
                        tfPlayer.stopTimer();
                    }
                });
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

    public void stop() {
        if (isRunning()) {
            System.out.println("Stopping timer...");
            this.scheduler.shutdownNow();
            this.scheduledFuture.cancel(true);
        }
    }

    public boolean isRunning() {
        return this.scheduledFuture != null && (!this.scheduledFuture.isCancelled() || !this.scheduledFuture.isDone());
    }

    public TFPlayer getPlayer(Player player) {
        TFPlayer tfPlayer = this.players.get(player);
        if (tfPlayer != null) {
            return this.players.get(player);
        }
        tfPlayer = new TFPlayer(player);
        this.players.put(player, tfPlayer);
        return tfPlayer;
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }
}
