package com.timedfly.managers;

import com.timedfly.TimedFly;
import com.timedfly.configurations.ConfigCache;
import com.timedfly.customevents.FlightTimeEndEvent;
import com.timedfly.customevents.FlightTimeStartEvent;
import com.timedfly.customevents.FlightTimeSubtractEvent;
import com.timedfly.listener.FallDamage;
import com.timedfly.utilities.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;
import java.util.UUID;

public class PlayerManager {
    private Plugin plugin;
    private Player player;
    private UUID uuid;
    private int timeLeft;
    private int initialTime;
    private boolean timePaused;
    private boolean timeManuallyPaused;
    private boolean inCombat;
    private boolean flying;
    private boolean inServer;
    private boolean timeEnded;
    private BossBarManager bossBarManager;
    private RefundManager refundManager;
    private MySQLManager sqlManager;
    private BukkitTask task;

    public PlayerManager(Plugin plugin, UUID uuid, int initialTime, int timeLeft) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.initialTime = initialTime;
        this.timeLeft = timeLeft;
        this.sqlManager = TimedFly.getMySqlManager();
        this.bossBarManager = new BossBarManager(uuid, (long) initialTime, (long) timeLeft, ConfigCache.getBossBarTimerColor(), ConfigCache.getBossBarTimerStyle());
        this.timePaused = false;
        this.flying = false;
        this.timeEnded = true;
        this.player = this.getPlayerFromUUID();
        this.refundManager = new RefundManager(this);
    }

    public PlayerManager(Plugin plugin, Player player, UUID uuid, int initialTime, int timeLeft) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.initialTime = initialTime;
        this.timeLeft = timeLeft;
        this.sqlManager = TimedFly.getMySqlManager();
        this.bossBarManager = new BossBarManager(uuid, (long) initialTime, (long) timeLeft, ConfigCache.getBossBarTimerColor(), ConfigCache.getBossBarTimerStyle());
        this.timePaused = false;
        this.flying = false;
        this.timeEnded = true;
        this.player = player;
        this.refundManager = new RefundManager(this);
    }

    public void startTimedFly() {
        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:startTimedFly: &7Start timed fly first line", 2);
        if (this.isInServer()) {
            if (this.timeLeft <= 0) {
                if (this.isFlying()) {
                    this.stopTimedFly(true, false);
                }

            } else if (!isFlying()) {
                Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:startTimedFly: &7Showing bossbar", 1);
                this.getBossBarManager().setCurrentTime((long) this.getTimeLeft()).setInitialTime((long) this.getInitialTime()).show();
                this.setTimeEnded(false);
                this.setFlying(true);
                this.setTimePaused(false);
                Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:startTimedFly: &7Starting runnable", 2);
                if (this.timeLeft > 0 && !this.timeEnded) {
                    cancelTask();
                    task = (new BukkitRunnable() {
                        public void run() {
                            if (PlayerManager.this.getTimeLeft() > 0) {
                                PlayerManager.this.setTimeLeft(PlayerManager.this.timeLeft--);
                                PlayerManager.this.timeLeft--;
                                FlightTimeSubtractEvent event = new FlightTimeSubtractEvent(PlayerManager.this.player, PlayerManager.this.uuid, PlayerManager.this.initialTime, PlayerManager.this.timeLeft, PlayerManager.this);
                                Bukkit.getServer().getPluginManager().callEvent(event);
                            } else {
                                PlayerManager.this.stopTimedFly(true, false);
                            }

                        }
                    }).runTaskTimer(this.plugin, 0L, 20L);
                }

                FlightTimeStartEvent event = new FlightTimeStartEvent(this.player, this.uuid, this.initialTime, this.timeLeft, this);
                Bukkit.getServer().getPluginManager().callEvent(event);
            }
        }
    }

    private void cancelTask() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void stopTimedFly(Boolean save, Boolean timePaused) {
        Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:stopTimedFly: &7Stop timed fly first line", 2);
        if (!this.isTimeEnded() && !this.isTimePaused()) {
            if (this.getBossBarManager().isRunning()) {
                Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:stopTimedFly: &7Hiding bossbar", 1);
                this.getBossBarManager().hide();
            }

            Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:stopTimedFly: &7stopping fly", 1);
            cancelTask();
            this.setTimeEnded(true);
            this.setTimePaused(timePaused);
            if (this.isInServer()) {
                if (TimedFly.getVersion().startsWith("v1_8")) {
                    FallDamage.setInvulnerable(true);
                } else {
                    this.getPlayerFromUUID().setInvulnerable(true);
                }

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    if (TimedFly.getVersion().startsWith("v1_8")) {
                        FallDamage.setInvulnerable(false);
                    } else {
                        this.getPlayerFromUUID().setInvulnerable(false);
                    }

                }, 120L);
                this.setFlying(false);
            }

            if (save) {
                this.sqlManager.saveData(this.getPlayerFromUUID(), this.getTimeLeft(), this.getInitialTime(), this.isTimeManuallyPaused());
            }

            Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:stopTimedFly: &7TimeLeft: " + this.getTimeLeft() + ", Initial: " + this.getInitialTime(), 1);
            FlightTimeEndEvent event = new FlightTimeEndEvent(this.player, this.uuid, this.initialTime, this.timeLeft, timePaused, this);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
    }

    public PlayerManager addTime(int time) {
        if (ConfigCache.isSkipFlightTimeIfHasPerm() && this.getPlayerFromUUID().hasPermission("timedfly.fly.onoff")) {
            return this;
        } else {
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

            if (this.isTimeManuallyPaused()) {
                return this;
            } else {
                if (this.isInServer() && !this.isTimePaused() && this.isTimeEnded()) {
                    Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:AddTime: &7Starting timed fly", 1);
                    this.startTimedFly();
                }

                return this;
            }
        }
    }

    public PlayerManager setTime(int time) {
        if (ConfigCache.isSkipFlightTimeIfHasPerm() && this.getPlayerFromUUID().hasPermission("timedfly.fly.onoff")) {
            return this;
        } else {
            Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:SetTime: &7setting time", 2);
            this.setTimeLeft(time);
            this.setInitialTime(time);
            if (this.isTimeManuallyPaused()) {
                return this;
            } else {
                if (this.isInServer() && !this.isTimePaused() && this.isTimeEnded()) {
                    Message.sendDebugMessage(this.getClass().getSimpleName() + "&c:SetTime: &7Starting timed fly", 2);
                    this.startTimedFly();
                }

                return this;
            }
        }
    }

    public boolean isInServer() {
        return this.inServer;
    }

    public PlayerManager setInServer(boolean inServer) {
        this.inServer = inServer;
        return this;
    }

    public BossBarManager getBossBarManager() {
        return this.bossBarManager;
    }

    public RefundManager getRefundManager() {
        return this.refundManager;
    }

    public Player getPlayerFromUUID() {
        Player player = Bukkit.getPlayer(this.uuid);
        return Objects.isNull(player) ? this.getPlayer() : player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public PlayerManager setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public int getInitialTime() {
        return this.initialTime;
    }

    public PlayerManager setInitialTime(int initialTime) {
        this.initialTime = initialTime;
        return this;
    }

    public int getTimeLeft() {
        return this.timeLeft;
    }

    public PlayerManager setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
        return this;
    }

    public boolean isTimePaused() {
        return this.timePaused;
    }

    public PlayerManager setTimePaused(boolean timePaused) {
        this.timePaused = timePaused;
        return this;
    }

    public boolean isTimeManuallyPaused() {
        return this.timeManuallyPaused;
    }

    public PlayerManager setTimeManuallyPaused(boolean timeManuallyPaused) {
        this.timeManuallyPaused = timeManuallyPaused;
        return this;
    }

    public boolean isFlying() {
        return this.flying;
    }

    public PlayerManager setFlying(boolean flying) {
        this.player.setAllowFlight(flying);
        this.player.setFlying(flying);
        this.flying = flying;
        return this;
    }

    public boolean isTimeEnded() {
        return this.timeEnded;
    }

    public PlayerManager setTimeEnded(boolean timeEnded) {
        this.timeEnded = timeEnded;
        return this;
    }

    public boolean isInCombat() {
        return this.inCombat;
    }

    public PlayerManager setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
        return this;
    }
}

