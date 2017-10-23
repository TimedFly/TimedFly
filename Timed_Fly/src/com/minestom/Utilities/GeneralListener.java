package com.minestom.Utilities;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.minestom.TimedFly;
import com.minestom.Updater.Updater;

public class GeneralListener implements Listener {

	public TimedFly plugin = TimedFly.getPlugin(TimedFly.class);
	
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
		Player player = event.getPlayer();
		if (GUIListener.cooldown.containsKey(player.getUniqueId())) {
			int x = player.getLocation().getBlockX();
			int y = player.getLocation().getBlockY();
			int z = player.getLocation().getBlockZ();
			int height = plugin.getConfig().getInt("Fly.JoinFlying.Height");
			player.setAllowFlight(true);
			if (plugin.getConfig().getBoolean("Fly.JoinFlying.Enabled") == true) {
				if (!player.isFlying()) {
					player.teleport(new Location(player.getLocation().getWorld(), x + 0.5, y + height, z + 0.5));
					player.setFlying(true);
				}
			}
		}
		if (player.hasPermission("timedfly.admin")) {
			Updater.sendUpdater(player);
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
}
