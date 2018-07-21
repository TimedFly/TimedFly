package com.timedfly.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FallDamage implements Listener {

    private static boolean invulnerable = false;

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if (!invulnerable) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        event.setCancelled(true);
    }

    public static void setInvulnerable(boolean invulnerable) {
        FallDamage.invulnerable = invulnerable;
    }
}
