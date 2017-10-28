package com.minestom.Utilities.Others;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.minestom.TimedFly;
import com.minestom.Languages.DataConfig;
import com.minestom.Languages.LangFiles;
import com.minestom.Updater.Updater;
import com.minestom.Utilities.GUI.GUIListener;

public class GeneralListener implements Listener {

	public TimedFly plugin = TimedFly.getPlugin(TimedFly.class);
	private static LangFiles lang = LangFiles.getInstance();
	private DataConfig data = DataConfig.getInstance();

	public GeneralListener() {
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				for (Map.Entry<UUID, Integer> entry : GUIListener.godmode.entrySet()) {
					Player player = Bukkit.getPlayer(entry.getKey());
					Integer time = entry.getValue();
					if (time == 0) {
						if (player != null) {
							GUIListener.godmode.remove(entry.getKey());
						}
						GUIListener.godmode.remove(player.getUniqueId());
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
		if (GUIListener.cooldown.containsKey(player.getUniqueId())) {
			int x = player.getLocation().getBlockX();
			int y = player.getLocation().getBlockY();
			int z = player.getLocation().getBlockZ();
			float pitch = player.getLocation().getPitch();
			float yaw = player.getLocation().getYaw();
			int height = config.getInt("Fly.JoinFlying.Height");
			player.setAllowFlight(true);
			if (config.getBoolean("Fly.JoinFlying.Enabled") == true) {
				if (!player.isFlying()) {
					player.teleport(
							new Location(player.getLocation().getWorld(), x + 0.5, y + height, z + 0.5, yaw, pitch));
					if (player.getAllowFlight() == true) {
						player.setFlying(true);
					}
				}
			}
		}
		if (plugin.getConfig().getBoolean("SaveTimeLeave") == true) {
			int time = data.getData().getInt(player.getUniqueId() + ".Time");
			if (data.getData().contains(player.getUniqueId().toString())) {
				GUIListener.cooldown.put(player.getUniqueId(), time);
				data.getData().set(player.getUniqueId().toString(), null);
				data.saveData();
			}
		}
		if (plugin.getConfig().getBoolean("Check-For-Updates") == true) {
			if (player.hasPermission("timedfly.admin")) {
				Updater.sendUpdater(player);
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (plugin.getConfig().getBoolean("SaveTimeLeave") == true) {
			if (GUIListener.cooldown.containsKey(player.getUniqueId())) {
				data.getData().set(player.getUniqueId() + ".Time", GUIListener.cooldown.get(player.getUniqueId()));
				data.saveData();
				GUIListener.cooldown.remove(player.getName());
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (GUIListener.godmode.containsKey(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onChangeWorld(PlayerTeleportEvent event) {
		World to = event.getTo().getWorld();
		Player player = event.getPlayer();
		List<String> worlds = plugin.getConfig().getStringList("Enabled-Worlds");
		if (!(event.getPlayer() instanceof Player)) {
			return;
		}
		for (String world : worlds) {
			World w = Bukkit.getWorld(world);
			if (to == w) {
				Bukkit.getScheduler().runTaskLater(plugin, new BukkitRunnable() {
					@Override
					public void run() {
						if (GUIListener.cooldown.containsKey(player.getUniqueId())) {
							player.setAllowFlight(true);
						}
					}
				}, 10L);
				
			} else {
				if (to != w) {
					if (GUIListener.cooldown.containsKey(player.getUniqueId())) {
						player.setAllowFlight(false);
						player.setFlying(false);
					}
				}
			}
		}
	}
}
