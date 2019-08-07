package me.jackint0sh.timedfly.listeners;

import me.jackint0sh.timedfly.events.TimedFlyEndEvent;
import me.jackint0sh.timedfly.events.TimedFlyRunningEvent;
import me.jackint0sh.timedfly.events.TimedFlyStartEvent;
import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import me.jackint0sh.timedfly.utilities.TimeParser;
import me.jackint0sh.timedfly.versions.ServerVersion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.stream.Collectors;

public class TimedFlyListener implements Listener {

    @EventHandler
    private void onFlyStart(TimedFlyStartEvent event) {

    }

    @EventHandler
    private void onFlyRunning(TimedFlyRunningEvent event) {
        PlayerManager playerManager = event.getPlayerManager();
        Player player = event.getPlayer();
        if (player == null || !player.isOnline()) return;

        String timeLeft = TimeParser.toReadableString(playerManager.getTimeLeft());

        if (Config.getConfig("config").get().getBoolean("Messages.ActionBar") && playerManager.getTimeLeft() > 0) {
            ServerVersion.getSupportedVersion().sendActionBar(player, "&aYou have &e" + timeLeft + "&a left");
        }

        List<Long> times = Config.getConfig("config").get().getStringList("Messages.Announcer.Times")
                .stream().map(TimeParser::parseNoException).collect(Collectors.toList());

        if (times.stream().anyMatch(time -> time == playerManager.getTimeLeft())) {
            if (Config.getConfig("config").get().getBoolean("Messages.Announcer.Titles")) {
                ServerVersion.getSupportedVersion().sendTitle(player,
                        "&eTime left:",
                        "&c" + timeLeft,
                        0, 45, 0
                );
            }
            if (Config.getConfig("config").get().getBoolean("Messages.Announcer.Chat")) {
                MessageUtil.sendMessage(player, "&7You have &e" + timeLeft + "&7 left");
            }
        }

    }

    @EventHandler
    private void onFlyEnd(TimedFlyEndEvent event) {
        PlayerManager playerManager = event.getPlayerManager();
        Player player = event.getPlayer();
        if (player == null || !player.isOnline()) return;

        String message = playerManager.getTimeLeft() <= 0 ? "&cFlight time ended." : "&cFlight time paused.";
        String subtitle = playerManager.getTimeLeft() <= 0 ? "&eBuy more on the store." : TimeParser.toReadableString(playerManager.getTimeLeft());

        if (Config.getConfig("config").get().getBoolean("Messages.ActionBar")) {
            ServerVersion.getSupportedVersion().sendActionBar(player, message);
        }

        if (Config.getConfig("config").get().getBoolean("Messages.Title")) {
            ServerVersion.getSupportedVersion().sendTitle(player, message, subtitle);
        }

        PlayerListener.handlePlayerQuery(playerManager, true);
    }

}
