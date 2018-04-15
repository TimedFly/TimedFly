package com.timedfly.Utilities;

import com.timedfly.Listeners.GUIListener;
import com.timedfly.Managers.MySQLManager;
import org.bukkit.entity.Player;

public class PlayerCache {

    private Player player;
    private int timeLeft;
    private int initialTime;
    private boolean timeStopped;
    private boolean timeOnHold;
    private boolean flying;
    private BossBarManager bossBarManager;

    public PlayerCache(MySQLManager sqlManager, Player player, BossBarManager bossBarManager) {
        this.player = player;
        this.bossBarManager = bossBarManager;
        this.timeLeft = sqlManager.getTimeLeft(player);
        this.initialTime = sqlManager.getInitialTime(player);
        this.timeStopped = sqlManager.isTimeStopped(player);
        this.timeOnHold = false;
        this.flying = false;
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
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

    public void setTimeStopped(boolean timeStopped) {
        this.timeStopped = timeStopped;
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

    public boolean isTimeOnHold() {
        return timeOnHold;
    }

    public void setTimeOnHold(boolean timeOnHold) {
        if (timeOnHold) GUIListener.flytime.remove(player.getUniqueId());
        else GUIListener.flytime.put(player.getUniqueId(), getTimeLeft());
        this.timeOnHold = timeOnHold;
    }

    public boolean isFlying() {
        return flying;
    }

    public void setFlying(boolean flying) {
        player.setAllowFlight(flying);
        player.setFlying(flying);
        this.flying = flying;
    }
}
