package com.timedfly.listener;

import com.timedfly.configurations.ConfigCache;
import com.timedfly.managers.PlayerManager;
import com.timedfly.utilities.Utilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class PlayerOnGround implements Listener {

    private Utilities utilities;

    public PlayerOnGround(Utilities utilities) {
        this.utilities = utilities;
    }

    @EventHandler
    public void onGround(PlayerMoveEvent event) {
        if (!ConfigCache.isStopFlyOnGround()) return;
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        if (!utilities.isWorldEnabled(player.getWorld())) return;
        if (!player.isOnGround()) return;

        PlayerManager playerManager = utilities.getPlayerManager(player.getUniqueId());

        if (playerManager.isTimePaused() || playerManager.isTimeEnded()) return;

        playerManager.stopTimedFly(false, true);
        player.setAllowFlight(true);

    }

    @EventHandler
    public void onFlightToggle(PlayerToggleFlightEvent event) {
        if (!ConfigCache.isStopFlyOnGround()) return;
        if (event.isCancelled()) return;
        if (!event.isFlying()) return;

        Player player = event.getPlayer();
        if (!utilities.isWorldEnabled(player.getWorld())) return;

        PlayerManager playerManager = utilities.getPlayerManager(player.getUniqueId());

        if (!playerManager.isTimePaused()) return;

        playerManager.startTimedFly();
    }
}
