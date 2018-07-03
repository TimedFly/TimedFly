package com.timedfly.listener;

import com.timedfly.configurations.ConfigCache;
import com.timedfly.managers.PlayerManager;
import com.timedfly.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.Plugin;

public class ChangeWorld implements Listener {

    private Utilities utility;
    private Plugin plugin;

    public ChangeWorld(Utilities utility, Plugin plugin) {
        this.utility = utility;
        this.plugin = plugin;
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        PlayerManager playerManager = utility.getPlayerManager(player.getUniqueId());

        if (playerManager == null) return;

        if (utility.isWorldEnabled(world)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!playerManager.isTimeEnded()) {
                    player.setAllowFlight(true);
                    playerManager.getBossBarManager().show();
                } else if (playerManager.isTimePaused()) {
                    playerManager.startTimedFly();
                }
            }, 8L);
        } else if (!playerManager.isTimeEnded()) {
            if (ConfigCache.isStopTimerOnBlackListedWorld()) {
                playerManager.stopTimedFly(false, true);
                playerManager.setTimePaused(true);
            } else {
                playerManager.getBossBarManager().hide();
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }
    }
}
