package com.minestom.Utilities.Others;

import com.minestom.Managers.MySQLManager;
import org.bukkit.entity.Player;

public class PlayerCache {

    private Player player;
    private int timeLeft;
    private int initialTime;
    private boolean timeStopped;

    public PlayerCache(MySQLManager sqlManager, Player player) {
        this.player = player;
        this.timeLeft = sqlManager.getTimeLeft(player);
        this.initialTime = sqlManager.getInitialTime(player);
        this.timeStopped = sqlManager.isTimeStopped(player);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isTimeStopped() {
        return timeStopped;
    }

    public int getInitialTime() {
        return initialTime;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setInitialTime(int initialTime) {
        this.initialTime = initialTime;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public void setTimeStopped(boolean timeStopped) {
        this.timeStopped = timeStopped;
    }
}
