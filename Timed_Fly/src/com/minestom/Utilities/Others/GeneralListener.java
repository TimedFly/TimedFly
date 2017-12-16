package com.minestom.Utilities.Others;

import com.minestom.ConfigurationFiles.LangFiles;
import com.minestom.Managers.MySQLManager;
import com.minestom.TimedFly;
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

import java.util.Map;
import java.util.UUID;

public class GeneralListener implements Listener {

    public TimedFly plugin = TimedFly.getInstance();
    private static LangFiles lang = LangFiles.getInstance();
    private MySQLManager sqlManager = new MySQLManager();
    private Utility utility = new Utility(plugin);

    public GeneralListener() {
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
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
        }, 0, 20L);

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        FileConfiguration config = lang.getLang();
        Player player = event.getPlayer();
        sqlManager.createPlayer(player);
        if (GUIListener.flytime.containsKey(player.getUniqueId())) {
            int x = player.getLocation().getBlockX();
            int y = player.getLocation().getBlockY();
            int z = player.getLocation().getBlockZ();
            float pitch = player.getLocation().getPitch();
            float yaw = player.getLocation().getYaw();
            int height = config.getInt("Fly.JoinFlying.Height");
            player.setAllowFlight(true);
            if (config.getBoolean("Fly.JoinFlying.Enabled") && !player.isFlying()) {
                player.teleport(new Location(player.getLocation().getWorld(), x + 0.5, y + height, z + 0.5, yaw, pitch));
                if (player.getAllowFlight()) {
                    player.setFlying(true);
                }
            }
        }
        int time = sqlManager.getTimeLeft(player);
        if (time != 0) {
            GUIListener.flytime.put(player.getUniqueId(), time);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (GUIListener.flytime.containsKey(player.getUniqueId())) {
            sqlManager.setTimeLeft(player, GUIListener.flytime.get(player.getUniqueId()));
            GUIListener.flytime.remove(player.getUniqueId());
        } else {
            if (sqlManager.getTimeLeft(player) != 0) {
                sqlManager.setTimeLeft(player, 0);
            }
        }
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
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        if (utility.isWorldEnabled(player)) {
            Bukkit.getScheduler().runTaskLater(plugin, new BukkitRunnable() {
                @Override
                public void run() {
                    if (GUIListener.flytime.containsKey(player.getUniqueId())) {
                        player.setAllowFlight(true);
                    }
                }
            }, 8L);
        } else {
            if (GUIListener.flytime.containsKey(player.getUniqueId())) {
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }
    }
}
