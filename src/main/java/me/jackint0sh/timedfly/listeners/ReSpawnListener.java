package me.jackint0sh.timedfly.listeners;

import me.jackint0sh.timedfly.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ReSpawnListener implements Listener {

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

}
