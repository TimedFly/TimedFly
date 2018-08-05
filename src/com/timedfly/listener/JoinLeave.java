package com.timedfly.listener;

import com.timedfly.TimedFly;
import com.timedfly.configurations.ConfigCache;
import com.timedfly.managers.MySQLManager;
import com.timedfly.managers.PlayerManager;
import com.timedfly.updater.Updater;
import com.timedfly.utilities.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeave implements Listener {

    private Utilities utilities;
    private MySQLManager sqlManager;
    private TimedFly plugin;
    private Updater updater;

    public JoinLeave(Utilities utilities, MySQLManager sqlManager, TimedFly plugin, Updater updater) {
        this.utilities = utilities;
        this.sqlManager = sqlManager;
        this.plugin = plugin;
        this.updater = updater;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        sqlManager.createPlayer(player);
        utilities.addPlayerManager(player.getUniqueId(), player, plugin);

        PlayerManager playerManager = utilities.getPlayerManager(player.getUniqueId());

        if (player.hasPermission("timedfly.getupdate")) updater.sendUpdateMessage(player);
        if (!utilities.isWorldEnabled(player.getWorld())) return;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            playerManager.setInServer(true).setInitialTime(sqlManager.getInitialTime(player)).setTimeLeft(sqlManager.getTimeLeft(player));
            if (!playerManager.isTimePaused()) playerManager.startTimedFly();
            if (playerManager.getTimeLeft() > 0 && ConfigCache.isJoinFlyingEnabled())
                player.teleport(player.getLocation().add(0, ConfigCache.getJoinFlyingHeight(), 0));
        }, 20);

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = utilities.getPlayerManager(player.getUniqueId());

        playerManager.setInServer(false);
        if (ConfigCache.isStopTimerOnLeave()) playerManager.stopTimedFly(false, true);
        sqlManager.saveData(player, playerManager.getTimeLeft(), playerManager.getInitialTime());
    }

}
