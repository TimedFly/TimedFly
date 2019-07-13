package me.jackint0sh.timedfly.listeners;

import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class AttackListener implements Listener {

    @EventHandler
    private void onAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        PlayerManager damagerManager = null;
        PlayerManager damagedManager = null;

        if (event.getDamager() instanceof Player)
            damagerManager = PlayerManager.getCachedPlayer(event.getDamager().getUniqueId());
        if (event.getEntity() instanceof Player)
            damagedManager = PlayerManager.getCachedPlayer(event.getDamager().getUniqueId());

        if (damagerManager != null) {
            if (damagerManager.isTimeRunning()) {
                damagerManager.getPlayer().setAllowFlight(false);
                damagerManager.getPlayer().setFlying(false);

                MessageUtil.sendMessage(damagerManager.getPlayer(), "Entering attack mode. Flight disabled!");
            }

            if (damagedManager != null && damagedManager.isTimeRunning()) {
                damagedManager.getPlayer().setAllowFlight(false);
                damagedManager.getPlayer().setFlying(false);

                MessageUtil.sendMessage(damagedManager.getPlayer(), "Entering attack mode. Flight disabled!");
            }

            PlayerManager finalDamagedManager = damagedManager;
            PlayerManager finalDamagerManager = damagerManager;

            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("TimedFly"), () -> {
                if (finalDamagerManager.isTimeRunning()) {
                    finalDamagerManager.getPlayer().setAllowFlight(true);
                    finalDamagerManager.getPlayer().setFlying(true);

                    MessageUtil.sendMessage(finalDamagerManager.getPlayer(), "Exiting attack mode. Flight re-enabled!");
                }

                if (finalDamagedManager != null && finalDamagedManager.isTimeRunning()) {
                    finalDamagedManager.getPlayer().setAllowFlight(true);
                    finalDamagedManager.getPlayer().setFlying(true);

                    MessageUtil.sendMessage(finalDamagedManager.getPlayer(), "Exiting attack mode. Flight re-enabled!");
                }
            }, 10 * 20L);
        }
    }

}
