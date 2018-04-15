package com.timedfly.Listeners;

import com.timedfly.ConfigurationFiles.ConfigCache;
import com.timedfly.ConfigurationFiles.LangFiles;
import com.timedfly.Managers.MySQLManager;
import com.timedfly.TimedFly;
import com.timedfly.Updater.Update;
import com.timedfly.Utilities.PlayerCache;
import com.timedfly.Utilities.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GeneralListener implements Listener {

    public TimedFly plugin;
    private MySQLManager sqlManager;
    private Utility utility;
    private Update updater;
    private ConfigCache configCache;

    public GeneralListener(TimedFly plugin, MySQLManager sqlManager, Utility utility, Update updater, ConfigCache configCache) {
        this.plugin = plugin;
        this.utility = utility;
        this.sqlManager = sqlManager;
        this.updater = updater;
        this.configCache = configCache;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        sqlManager.createPlayer(player);

        if (player.hasPermission("timedfly.getupdate")) updater.sendUpdateMessage(player);

        if (!utility.getPlayerCacheMap().containsKey(player.getUniqueId())) utility.addPlayerCache(player, sqlManager);
        PlayerCache playerCache = utility.getPlayerCache(player);


        if (!configCache.isStopTimerOnLeave()) return;
        if (playerCache.isTimeStopped()) return;

        int time = playerCache.getTimeLeft();
        if (time != 0) GUIListener.flytime.put(player.getUniqueId(), time);

        if (GUIListener.flytime.containsKey(player.getUniqueId())) {
            int x = player.getLocation().getBlockX();
            int y = player.getLocation().getBlockY();
            int z = player.getLocation().getBlockZ();
            float pitch = player.getLocation().getPitch();
            float yaw = player.getLocation().getYaw();
            int height = configCache.getJoinFlyingHeight();
            player.setAllowFlight(true);
            if (configCache.isJoinFlyingEnabled() && !player.isFlying()) {
                player.teleport(new Location(player.getLocation().getWorld(), x + 0.5, y + height, z + 0.5, yaw, pitch));
                if (player.getAllowFlight() && player.isOnGround()) player.setFlying(true);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerCache playerCache = utility.getPlayerCache(player);

        if (GUIListener.flytime.containsKey(player.getUniqueId()) && configCache.isStopTimerOnLeave()) {
            playerCache.setTimeLeft(GUIListener.flytime.get(player.getUniqueId()));
            GUIListener.flytime.remove(player.getUniqueId());
        }

        sqlManager.setTimeStopped(player, playerCache.isTimeStopped());
        sqlManager.setTimeLeft(player, playerCache.getTimeLeft());
        sqlManager.setInitialTime(player, playerCache.getInitialTime());

        utility.getPlayerCacheMap().remove(player.getUniqueId());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && GUIListener.after.contains(player.getUniqueId())) {
                event.setCancelled(true);
                GUIListener.after.remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onChangeWorld(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        PlayerCache playerCache = utility.getPlayerCache(player);

        if (playerCache == null) return;

        if (utility.isWorldEnabled(event.getTo().getWorld())) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (GUIListener.flytime.containsKey(player.getUniqueId())) {
                    player.setAllowFlight(true);
                    if (configCache.isBossBarTimerEnabled()) playerCache.getBossBarManager().show();
                } else if (playerCache.isTimeOnHold()) {
                    playerCache.setTimeOnHold(false);
                    player.setAllowFlight(true);
                }
            }, 8L);
        } else if (GUIListener.flytime.containsKey(player.getUniqueId())) {
            if (configCache.isStopTimerOnBlackListedWorld()) {
                playerCache.setTimeLeft(GUIListener.flytime.get(player.getUniqueId()));
                playerCache.setTimeOnHold(true);
                GUIListener.flytime.remove(player.getUniqueId());
            }
            player.setAllowFlight(false);
            player.setFlying(false);

            if (configCache.isBossBarTimerEnabled()) playerCache.getBossBarManager().hide();
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!configCache.isStopFlyOnAttack()) return;
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            PlayerCache playerCache = utility.getPlayerCache(player);
            if (!playerCache.isFlying()) return;
            playerCache.setFlying(false);
            Bukkit.getScheduler().runTaskLater(plugin, () -> playerCache.setFlying(true), 5 * 20L);
            utility.message(player, LangFiles.getInstance().getLang().getString("Other.OnCombat"));
        }
        if (event.getEntity() instanceof Player) {
            Player player2 = (Player) event.getEntity();
            PlayerCache playerCache2 = utility.getPlayerCache(player2);
            if (!playerCache2.isFlying()) return;
            playerCache2.setFlying(false);
            Bukkit.getScheduler().runTaskLater(plugin, () -> playerCache2.setFlying(true), 10 * 20L);
            utility.message(player2, LangFiles.getInstance().getLang().getString("Other.OnCombat"));
        }
    }
}
