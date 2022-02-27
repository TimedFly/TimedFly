package me.jackscode.timedfly.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.jackscode.timedfly.api.entity.TFPlayer;

public class FallDamage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled())
            return;
        Entity entity = event.getEntity();
        if (entity.isDead() || event.getCause().compareTo(DamageCause.FALL) != 0)
            return;
        TFPlayer tfPlayer = TFPlayer.getPlayer((Player) entity);
        if (tfPlayer != null && tfPlayer.isPreventFallDamage()) {
            tfPlayer.setPreventFallDamage(false);
            event.setCancelled(true);
        }
    }

}
