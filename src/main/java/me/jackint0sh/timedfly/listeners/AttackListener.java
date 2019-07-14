package me.jackint0sh.timedfly.listeners;

import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class AttackListener implements Listener {

    @EventHandler
    private void onAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        PlayerManager damagerManager = null;
        PlayerManager damagedManager = null;

        if (event.getDamager() instanceof Player) {
            damagerManager = PlayerManager.getCachedPlayer(event.getDamager().getUniqueId());
        }
        if (event.getEntity() instanceof Player) {
            damagedManager = PlayerManager.getCachedPlayer(event.getEntity().getUniqueId());
        }

        if (damagerManager != null) damagerManager.enterAttackMode();
        if (damagedManager != null) damagedManager.enterAttackMode();

    }

    @EventHandler
    public void onAttackBow(ProjectileHitEvent event) {
        if (event.getHitBlock() != null) return;

        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());

            if (playerManager != null && playerManager.isTimeRunning()) {
                playerManager.disableFallDamage();
                player.setAllowFlight(false);
                player.setFlying(false);

                MessageUtil.sendMessage(player, "Entering attack mode. Flight disabled!");

                Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0], () -> {
                    if (playerManager.isTimeRunning()) {
                        player.setAllowFlight(true);
                        player.setFlying(true);

                        MessageUtil.sendMessage(player, "Exiting attack mode. Flight re-enabled!");
                    }
                }, 10 * 20L);
            }
        }
    }
}
