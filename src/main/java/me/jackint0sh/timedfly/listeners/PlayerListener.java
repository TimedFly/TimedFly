package me.jackint0sh.timedfly.listeners;

import me.jackint0sh.timedfly.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onReSpawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

        if (playerManager != null && playerManager.isTimeRunning()) {
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("TimedFly"), () -> {
                player.setAllowFlight(true);
                player.setFlying(true);
            }, 2);
        }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
            if (playerManager != null && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                if (!playerManager.isDamageTimerEnabled()) playerManager.callEvent(event);
                event.setCancelled(playerManager.isDamageTimerEnabled());
            }
        }
    }

    @EventHandler
    private void onGameCodeChange(PlayerGameModeChangeEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
        if (playerManager != null && event.getNewGameMode() == GameMode.SURVIVAL && playerManager.isTimeRunning()) {
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("TimedFly"), () -> {
                player.setAllowFlight(true);
                player.setFlying(true);
            }, 2);
        }
    }

    @EventHandler
    public void onGround(PlayerMoveEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

        if (player.isOnGround() && playerManager != null) {
            if (!playerManager.isOnFloor() && playerManager.hasTime() && playerManager.isTimeRunning()) {
                playerManager.setOnFloor(true).setTimeRunning(false);
            }
        }
    }

    @EventHandler
    public void onFlightToggle(PlayerToggleFlightEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

        if (playerManager != null && playerManager.isOnFloor()) {
            playerManager.setOnFloor(false);
            if (!playerManager.hasTime()) {
                event.setCancelled(true);
                playerManager.stopTimer();
                return;
            }
            playerManager.startTimer();
        }
    }
}
