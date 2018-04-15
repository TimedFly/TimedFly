package com.timedfly.Hooks;

import com.timedfly.Listeners.GUIListener;
import com.wasteofplastic.askyblock.events.IslandEnterEvent;
import com.wasteofplastic.askyblock.events.IslandExitEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class aSkyblock implements Listener {

    @EventHandler
    public void onIslandEnter(IslandEnterEvent e) {
        UUID uuid = e.getPlayer();
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (GUIListener.flytime.containsKey(uuid)) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onIslandExit(IslandExitEvent e) {
        UUID uuid = e.getPlayer();
        Player player = Bukkit.getServer().getPlayer(uuid);

        if (GUIListener.flytime.containsKey(uuid)) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
    }

}
