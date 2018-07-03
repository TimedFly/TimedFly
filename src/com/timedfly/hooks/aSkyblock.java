package com.timedfly.hooks;

import com.timedfly.managers.PlayerManager;
import com.timedfly.utilities.Utilities;
import com.wasteofplastic.askyblock.events.IslandEnterEvent;
import com.wasteofplastic.askyblock.events.IslandExitEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class aSkyblock implements Listener {

    private Utilities utilities;

    public aSkyblock(Utilities utilities) {
        this.utilities = utilities;
    }

    @EventHandler
    public void onIslandEnter(IslandEnterEvent e) {
        UUID uuid = e.getPlayer();
        Player player = Bukkit.getServer().getPlayer(uuid);
        PlayerManager playerManager = utilities.getPlayerManager(uuid);

        if (playerManager.getTimeLeft() > 0) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onIslandExit(IslandExitEvent e) {
        UUID uuid = e.getPlayer();
        Player player = Bukkit.getServer().getPlayer(uuid);
        PlayerManager playerManager = utilities.getPlayerManager(uuid);

        if (playerManager.getTimeLeft() > 0) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }

}
