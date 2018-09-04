package com.timedfly.listener;

import com.timedfly.configurations.ConfigCache;
import com.timedfly.configurations.Languages;
import com.timedfly.managers.PlayerManager;
import com.timedfly.utilities.Message;
import com.timedfly.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.Plugin;

public class AttackMob implements Listener {

    private Plugin plugin;
    private Utilities utility;
    private Languages languages;

    public AttackMob(Plugin plugin, Utilities utility, Languages languages) {
        this.plugin = plugin;
        this.utility = utility;
        this.languages = languages;
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (!ConfigCache.isStopFlyOnAttack()) return;
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            if (!player.hasPermission("timedfly.attack.bypass") || !player.hasPermission("timedfly.admin")) return;
            if (!utility.isWorldEnabled(player.getWorld())) return;
            PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());

            if (playerManager == null || !playerManager.isFlying() || playerManager.isTimeEnded() || playerManager.isTimePaused()
                    || playerManager.isTimeManuallyPaused() || playerManager.isInCombat()) return;

            playerManager.setFlying(false);
            Bukkit.getScheduler().runTaskLater(plugin, () -> playerManager.setFlying(true), 10 * 20L);
            Message.sendMessage(player, languages.getLanguageFile().getString("Other.OnCombat"));
        }
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!utility.isWorldEnabled(player.getWorld())) return;
            PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());

            if (playerManager == null || !playerManager.isFlying() || playerManager.isTimeEnded() || playerManager.isTimePaused()
                    || playerManager.isTimeManuallyPaused() || playerManager.isInCombat()) return;

            playerManager.setFlying(false).setInCombat(true);
            Bukkit.getScheduler().runTaskLater(plugin, () -> playerManager.setFlying(true).setInCombat(false), 10 * 20L);
            Message.sendMessage(player, languages.getLanguageFile().getString("Other.OnCombat"));
        }
    }

    @EventHandler
    public void onAttackBow(ProjectileHitEvent event) {
        if (!ConfigCache.isStopFlyOnAttack()) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        if (event.getHitBlock() != null) return;

        Player player = (Player) event.getEntity().getShooter();
        if (!player.hasPermission("timedfly.attack.bypass") || !player.hasPermission("timedfly.admin")) return;
        if (!utility.isWorldEnabled(player.getWorld())) return;
        PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());

        if (playerManager == null || !playerManager.isFlying() || playerManager.isTimeEnded() || playerManager.isTimePaused()
                || playerManager.isTimeManuallyPaused() || playerManager.isInCombat()) return;

        playerManager.setFlying(false).setInCombat(true);
        Bukkit.getScheduler().runTaskLater(plugin, () -> playerManager.setFlying(true).setInCombat(false), 10 * 20L);
        Message.sendMessage(player, languages.getLanguageFile().getString("Other.OnCombat"));
    }
}
