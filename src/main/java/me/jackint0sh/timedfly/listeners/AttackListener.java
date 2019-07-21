package me.jackint0sh.timedfly.listeners;

import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.utilities.Config;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AttackListener implements Listener {

    @EventHandler
    private void onAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        Entity entity = event.getEntity();

        PlayerManager damagerManager = null;
        PlayerManager damagedManager = null;

        if (event.getDamager() instanceof Player) {
            damagerManager = PlayerManager.getCachedPlayer(event.getDamager().getUniqueId());
        }
        if (entity instanceof Player) {
            damagedManager = PlayerManager.getCachedPlayer(event.getEntity().getUniqueId());
        }

        if (damagedManager != null) damagedManager.enterAttackMode();
        if (damagerManager != null) {
            List<String> excludedEntities = getExcludedEntities();
            if (excludedEntities.contains(entity.getType().name())) return;

            List<String> entityConfig = Config.getConfig("config").get()
                    .getStringList("StopTimerOn.Attack.Entity")
                    .stream().filter(e -> Arrays.stream(EntityType.values()).anyMatch(et -> !et.name().equals(e)))
                    .collect(Collectors.toList());
            for (String s : entityConfig) {
                if (excludedEntities.contains(s.substring(1).toUpperCase())) continue;
                switch (s) {
                    case "player":
                    case "players":
                        if (entity instanceof Player) damagerManager.enterAttackMode();
                        break;
                    case "mob":
                    case "mobs":
                        if (entity instanceof Creature) damagerManager.enterAttackMode();
                        break;
                    case "hostile":
                        if (entity instanceof Monster) damagerManager.enterAttackMode();
                        break;
                    case "pacific":
                        if (!(entity instanceof Monster)) damagerManager.enterAttackMode();
                        break;
                    case "all":
                        damagerManager.enterAttackMode();
                        break;
                }
            }
            if (getIncludedEntities().contains(entity.getType().name())) damagerManager.enterAttackMode();
        }

    }

    @EventHandler
    public void onAttackBow(ProjectileHitEvent event) {
        if (event.getHitBlock() != null) return;

        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();
            PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
            if (playerManager != null) playerManager.enterAttackMode();
        }
    }

    private List<String> getExcludedEntities() {
        List<String> entities = Config.getConfig("config").get().getStringList("StopTimerOn.Attack.Entity");
        System.out.println(entities.toString());
        return entities.stream()
                .filter(entity -> entity.startsWith("!"))
                .map(entity -> entity.toUpperCase().substring(1))
                .collect(Collectors.toList());
    }

    private List<String> getIncludedEntities() {
        List<String> entities = Config.getConfig("config").get().getStringList("StopTimerOn.Attack.Entity");
        return entities.stream()
                .filter(entity -> !entity.startsWith("!"))
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }
}
