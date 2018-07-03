package com.timedfly.customevents;

import com.timedfly.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class FlightTimeSubtractEvent extends Event {

    private Player player;
    private UUID uuid;
    private int initialTime;
    private int timeLeft;
    private PlayerManager playerManager;

    private static final HandlerList handlers = new HandlerList();

    public FlightTimeSubtractEvent(Player player, UUID uuid, int initialTime, int timeLeft, PlayerManager playerManager) {
        this.player = player;
        this.uuid = uuid;
        this.initialTime = initialTime;
        this.timeLeft = timeLeft;
        this.playerManager = playerManager;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getInitialTime() {
        return initialTime;
    }

    public void setInitialTime(int initialTime) {
        this.initialTime = initialTime;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
