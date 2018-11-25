package com.timedfly.listener;

import com.timedfly.TimedFly;
import com.timedfly.configurations.ConfigCache;
import com.timedfly.managers.MySQLManager;
import com.timedfly.managers.PlayerManager;
import com.timedfly.updater.Updater;
import com.timedfly.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeave implements Listener {
    private Utilities utilities;
    private MySQLManager sqlManager;
    private TimedFly plugin;
    private Updater updater;

    public JoinLeave(Utilities utilities, MySQLManager sqlManager, TimedFly plugin, Updater updater) {
        this.utilities = utilities;
        this.sqlManager = sqlManager;
        this.plugin = plugin;
        this.updater = updater;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.sqlManager.createPlayer(player);
        this.utilities.addPlayerManager(player.getUniqueId(), player, this.plugin);
        PlayerManager playerManager = this.utilities.getPlayerManager(player.getUniqueId());
        if (player.hasPermission("timedfly.getupdate")) {
            this.updater.sendUpdateMessage(player);
        }

        if (this.utilities.isWorldEnabled(player.getWorld())) {
            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                playerManager.setInServer(true).setInitialTime(this.sqlManager.getInitialTime(player)).setTimeLeft(this.sqlManager.getTimeLeft(player)).setTimeManuallyPaused(this.sqlManager.getManuallyStopped(player));
                if (playerManager.getTimeLeft() >= 1) {
                    if (!playerManager.isTimePaused() && !playerManager.isTimeManuallyPaused()) {
                        playerManager.startTimedFly();
                    }

                    if (ConfigCache.isJoinFlyingEnabled()) {
                        player.teleport(player.getLocation().add(0.0D, (double) ConfigCache.getJoinFlyingHeight(), 0.0D));
                    }

                }
            }, 20L);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = this.utilities.getPlayerManager(player.getUniqueId());
        playerManager.setInServer(false);
        if (ConfigCache.isStopTimerOnLeave()) {
            playerManager.stopTimedFly(false, true);
        }

        sqlManager.saveDataAsync(player, playerManager.getTimeLeft(), playerManager.getInitialTime(), playerManager.isTimeManuallyPaused());
    }
}
