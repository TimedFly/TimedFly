package me.jackint0sh.timedfly.listeners;

import me.jackint0sh.timedfly.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
            if (playerManager != null) {
                if (!playerManager.isDamageTimerEnabled()) playerManager.callEvent(event);
                event.setCancelled(playerManager.isDamageTimerEnabled());
            }
        }
    }

}
