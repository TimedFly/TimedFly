package me.jackint0sh.timedfly.events;

import me.jackint0sh.timedfly.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimedFlyStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private PlayerManager playerManager;
    private Player player;

    public TimedFlyStartEvent(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
