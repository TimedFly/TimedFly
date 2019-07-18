package me.jackint0sh.timedfly.listeners;

import me.jackint0sh.timedfly.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void onReSpawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

        if (playerManager != null && playerManager.isTimeRunning()) {
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0], () -> {
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
            if (playerManager != null && !playerManager.isFallDamageEnabled() && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                playerManager.enableFallDamage();
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
        if (playerManager != null && (event.getNewGameMode() == GameMode.SURVIVAL || event.getNewGameMode() == GameMode.ADVENTURE)
                && playerManager.isTimeRunning()) {
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0], () -> {
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
            playerManager.setOnFloor(true);
            if (playerManager.hasTime() && playerManager.isTimeRunning() && !playerManager.isAttacking()) {
                if (!player.getAllowFlight()) player.setAllowFlight(true);
                playerManager.setTimeRunning(false);
            }
        }
    }

    @EventHandler
    public void onFlightToggle(PlayerToggleFlightEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

        if (playerManager != null && playerManager.isOnFloor()) {
            if (!playerManager.hasTime()) {
                event.setCancelled(true);
                playerManager.stopTimer();
                return;
            }
            playerManager.setOnFloor(false);
            playerManager.startTimer();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0], () -> {
            Player player = event.getPlayer();
            PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

            if (playerManager != null) {
                // TODO: Database implementation
                playerManager.setOnFloor(player.isOnGround()).setPlayer(player);
                if (!playerManager.isTimePaused() && playerManager.hasTime()) playerManager.startTimer();
            }
        }, 20);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

        if (playerManager != null) {
            // TODO: Database implementation
        }
    }
}
