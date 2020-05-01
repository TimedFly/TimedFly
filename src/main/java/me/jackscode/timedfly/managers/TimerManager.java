package me.jackscode.timedfly.managers;

import lombok.Getter;
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
        scheduler = Executors.newSingleThreadScheduledExecutor();
        players = new IdentityHashMap<>();
    }

    public void start() {
        if (!isRunning()) {
            scheduledFuture = scheduler.scheduleAtFixedRate(() -> {
                if (players.isEmpty()) return;
                players.forEach((player, tfPlayer) -> {
                    Bukkit.getPluginManager().callEvent(new TimedFlyRunningEvent(tfPlayer));
                });
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

    public void stop() {
        if (isRunning()) {
            scheduler.shutdownNow();
            scheduledFuture.cancel(true);
        }
    }

    public boolean isRunning() {
        return scheduledFuture != null && (!scheduledFuture.isCancelled() || !scheduledFuture.isDone());
    }

    public TFPlayer getPlayer(Player player) {
        return players.containsKey(player) ? players.get(player) : new TFPlayer(player);
    }
}
