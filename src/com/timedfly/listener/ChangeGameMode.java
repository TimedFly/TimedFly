package com.timedfly.listener;

import com.timedfly.managers.PlayerManager;
import com.timedfly.utilities.Utilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

public class ChangeGameMode implements Listener {

    private Utilities utility;

    public ChangeGameMode(Utilities utility) {
        this.utility = utility;
    }

    @EventHandler
    public void onGameMode(PlayerGameModeChangeEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());

        if (playerManager == null) return;
        if (!utility.isWorldEnabled(player.getWorld())) return;
        if (playerManager.getTimeLeft() < 1) {
            playerManager.stopTimedFly(true, false);
            return;
        }
        if (playerManager.isTimeManuallyPaused()) return;
        if (!playerManager.isFlying() || !playerManager.isTimeEnded() || !playerManager.isTimePaused()) return;

        playerManager.setFlying(true);
    }

}
