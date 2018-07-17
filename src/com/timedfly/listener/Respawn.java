package com.timedfly.listener;

import com.timedfly.managers.PlayerManager;
import com.timedfly.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

public class Respawn implements Listener {

    private Utilities utilities;
    private Plugin plugin;

    public Respawn(Utilities utilities, Plugin plugin) {
        this.utilities = utilities;
        this.plugin = plugin;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        World world = event.getRespawnLocation().getWorld();
        if (!utilities.isWorldEnabled(world)) return;

        Player player = event.getPlayer();
        PlayerManager playerManager = utilities.getPlayerManager(player.getUniqueId());

        if (playerManager.isTimePaused() || playerManager.isTimeEnded()) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> playerManager.setFlying(true), 5L);
    }
}
