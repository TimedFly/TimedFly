package com.timedfly.managers;

import com.timedfly.configurations.ConfigCache;
import com.timedfly.customevents.FlightTimeEndEvent;
import com.timedfly.customevents.FlightTimeStartEvent;
import com.timedfly.customevents.FlightTimeSubtractEvent;
import com.timedfly.utilities.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PlayerManager {

    private Plugin plugin;
    private Player player;
    private UUID uuid;
    private int timeLeft;
    private int initialTime;
    private boolean timePaused;
    private boolean flying;
    private boolean inServer;
    private boolean timeEnded;
    private BossBarManager bossBarManager;
    private MySQLManager sqlManager;
    private int taskId;

    public PlayerManager(Plugin plugin, UUID uuid, int initialTime, int timeLeft, MySQLManager sqlManager) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.initialTime = initialTime;
        this.timeLeft = timeLeft;
        this.sqlManager = sqlManager;
        this.bossBarManager = new BossBarManager(uuid, initialTime, timeLeft);
        this.timePaused = false;
        this.flying = false;
        this.timeEnded = true;
        this.player = getPlayerFromUUID();

        bossBarManager.setBarColor(ConfigCache.getBossBarTimerColor()).setBarStyle(ConfigCache.getBossBarTimerStyle());
    }

    public void startTimedFly() {
        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:startTimedFly: &7Start timed fly first line", 2);
        if (!this.isInServer()) return;
        if (this.timeLeft <= 0) return;

        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:startTimedFly: &7Showing bossbar", 1);
        this.getBossBarManager().setCurrentTime(getTimeLeft()).setInitialTime(getInitialTime()).show();

        this.setTimeEnded(false);
        this.setFlying(true);
        this.setTimePaused(false);

        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:startTimedFly: &7Starting runnable", 2);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (getTimeLeft() > 0) {
                    setTimeLeft(timeLeft--);
                    timeLeft--;

                    FlightTimeSubtractEvent event = new FlightTimeSubtractEvent(player, uuid, initialTime, timeLeft, PlayerManager.this);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                } else stopTimedFly(true, false);

                taskId = this.getTaskId();
            }
        }.runTaskTimer(plugin, 0L, 20L);

        FlightTimeStartEvent event = new FlightTimeStartEvent(this.player, this.uuid, this.initialTime, this.timeLeft, this);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void stopTimedFly(Boolean save, Boolean timePaused) {
        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:stopTimedFly: &7Stop timed fly first line", 2);

        if (this.isTimeEnded() || this.isTimePaused()) return;
        if (save) this.sqlManager.saveData(getPlayerFromUUID(), getTimeLeft(), getInitialTime());

        if (this.getBossBarManager().isRunning()) {
            Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:stopTimedFly: &7Hiding bossbar", 1);
            this.getBossBarManager().hide();
        }

        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:stopTimedFly: &7stopping fly", 1);
        Bukkit.getScheduler().cancelTask(taskId);

        if (this.isInServer()) {
            getPlayerFromUUID().setInvulnerable(true);
            Bukkit.getScheduler().runTaskLater(plugin, () -> getPlayerFromUUID().setInvulnerable(false), 6 * 20);
            this.setFlying(false);
        }

        this.setTimeEnded(true).setTimePaused(timePaused);
        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:stopTimedFly: &7TimeLeft: " + getTimeLeft() + ", Initial: " + getInitialTime(), 1);

        FlightTimeEndEvent event = new FlightTimeEndEvent(this.player, this.uuid, this.initialTime, this.timeLeft, timePaused, this);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void stopTimedFly() {
        stopTimedFly(false, false);
    }

    public PlayerManager addTime(int time) {
        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:addTime: &7Adding time method", 1);
        if (this.getTimeLeft() > 0) {
            Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:AddTime: &7Adding time because > 0", 1);
            this.setTimeLeft(this.getTimeLeft() + time);
            this.setInitialTime(this.getInitialTime() + time);
        } else {
            Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:AddTime: &7Setting time because < 0", 1);
            this.setTimeLeft(time);
            this.setInitialTime(time);
        }

        if (this.isInServer() && !this.isTimePaused() && this.isTimeEnded()) {
            Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:AddTime: &7Starting timed fly", 1);
            this.startTimedFly();
        }

        return this;
    }

    public PlayerManager setTime(int time) {
        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:SetTime: &7setting time", 2);
        this.setTimeLeft(time);
        this.setInitialTime(time);

        if (this.isInServer() && !this.isTimePaused() && this.isTimeEnded()) {
            Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:SetTime: &7Starting timed fly", 2);
            this.startTimedFly();
        }
        return this;
    }

    public boolean isInServer() {
        return inServer;
    }

    public PlayerManager setInServer(boolean inServer) {
        this.inServer = inServer;
        return this;
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public Player getPlayerFromUUID() {
        return Bukkit.getPlayer(uuid);
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerManager setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public int getInitialTime() {
        return initialTime;
    }

    public PlayerManager setInitialTime(int initialTime) {
        this.initialTime = initialTime;
        return this;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public PlayerManager setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
        return this;
    }

    public boolean isTimePaused() {
        return timePaused;
    }

    public PlayerManager setTimePaused(boolean timePaused) {
        this.timePaused = timePaused;
        return this;
    }

    public boolean isFlying() {
        return flying;
    }

    public PlayerManager setFlying(boolean flying) {
        player.setAllowFlight(flying);
        player.setFlying(flying);
        this.flying = flying;
        return this;
    }

    public boolean isTimeEnded() {
        return timeEnded;
    }

    public PlayerManager setTimeEnded(boolean timeEnded) {
        this.timeEnded = timeEnded;
        return this;
    }
}
