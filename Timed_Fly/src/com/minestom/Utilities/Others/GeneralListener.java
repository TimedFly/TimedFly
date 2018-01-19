package com.minestom.Utilities.Others;

import com.minestom.Managers.MySQLManager;
import com.minestom.TimedFly;
import com.minestom.Utilities.BossBarManager;
import com.minestom.Utilities.GUI.GUIListener;
import com.minestom.Utilities.Utility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GeneralListener implements Listener {

    public TimedFly plugin;
    private MySQLManager sqlManager;
    private Utility utility;
    public static Map<UUID, Integer> initialTime = new HashMap<>();


    public GeneralListener(TimedFly plugin, MySQLManager sqlManager, Utility utility) {
        this.plugin = plugin;
        this.utility = utility;
        this.sqlManager = sqlManager;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Integer> entry : GUIListener.godmode.entrySet()) {
                    Player player = Bukkit.getPlayer(entry.getKey());
                    Integer time = entry.getValue();
                    if (time == 0) {
                        if (player == null) {
                            GUIListener.godmode.remove(entry.getKey());
                        } else {
                            GUIListener.godmode.remove(player.getUniqueId());
                        }
                    } else {
                        GUIListener.godmode.put(entry.getKey(), time - 1);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        FileConfiguration config = plugin.getConfig();
        Player player = event.getPlayer();

        if (!utility.getPlayerCacheMap().containsKey(player)) utility.getPlayerCacheMap().put(player, new PlayerCache(sqlManager, player));
        PlayerCache playerCache = utility.getPlayerCacheMap().get(player);

        sqlManager.createPlayer(player);

        if (!plugin.getConfig().getBoolean("StopTimerOnLeave")) return;
        if (playerCache.isTimeStopped()) return;

        int time = playerCache.getTimeLeft();
        if (time != 0) GUIListener.flytime.put(player.getUniqueId(), time);

        if (GUIListener.flytime.containsKey(player.getUniqueId())) {
            int x = player.getLocation().getBlockX();
            int y = player.getLocation().getBlockY();
            int z = player.getLocation().getBlockZ();
            float pitch = player.getLocation().getPitch();
            float yaw = player.getLocation().getYaw();
            int height = config.getInt("JoinFlying.Height");
            player.setAllowFlight(true);
            if (config.getBoolean("JoinFlying.Enabled") && !player.isFlying()) {
                player.teleport(new Location(player.getLocation().getWorld(), x + 0.5, y + height, z + 0.5, yaw, pitch));
                if (player.getAllowFlight() && player.isOnGround()) player.setFlying(true);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerCache playerCache = utility.getPlayerCacheMap().get(player);

        if (GUIListener.flytime.containsKey(player.getUniqueId()) && plugin.getConfig().getBoolean("StopTimerOnLeave")) {
            playerCache.setTimeLeft(GUIListener.flytime.get(player.getUniqueId()));
            GUIListener.flytime.remove(player.getUniqueId());
        }

        sqlManager.setTimeStopped(player, playerCache.isTimeStopped());
        sqlManager.setTimeLeft(player, playerCache.getTimeLeft());
        sqlManager.setInitialTime(player, playerCache.getInitialTime());

        utility.getPlayerCacheMap().remove(player);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL && GUIListener.godmode.containsKey(player.getUniqueId())) {
                event.setCancelled(true);
                GUIListener.godmode.remove(player.getUniqueId());
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onChangeWorld(PlayerTeleportEvent event) {
        BossBarManager bossBarManager = plugin.getBossBarManager();
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (utility.isWorldEnabled(player, event.getTo().getWorld())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (GUIListener.flytime.containsKey(player.getUniqueId())) {
                        player.setAllowFlight(true);
                        if (plugin.getConfig().getBoolean("BossBarTimer.Enabled")) {
                            bossBarManager.addPlayer(player);
                        }
                    }
                }
            }.runTaskLater(plugin, 8L);
        } else {
            if (GUIListener.flytime.containsKey(player.getUniqueId())) {
                if (plugin.getConfig().getBoolean("BossBarTimer.Enabled")) {
                    bossBarManager.removeBar(player);
                }
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }
    }
}
