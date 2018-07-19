package com.timedfly.managers;

import com.timedfly.TimedFly;
import com.timedfly.configurations.ConfigCache;
import com.timedfly.utilities.Bossbars;
import com.timedfly.utilities.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BossBarManager {

    private long initialTime;
    private String title;
    private String color;
    private String style;
    private long currentTime;
    private boolean running;
    private Player player;
    private UUID uuid;
    private Bossbars bar;

    public BossBarManager(UUID uuid, String title, String color, String style, long initialTime, long currentTime) {
        this.uuid = uuid;
        this.player = getPlayerFromUUID();
        this.title = title;
        this.color = color.toUpperCase();
        this.style = style.toUpperCase();
        this.initialTime = initialTime;
        this.currentTime = currentTime;
        if (!TimedFly.getVersion().startsWith("v1_8")) this.bar = new Bossbars(title, color, style);
        this.running = false;
    }

    public BossBarManager(UUID uuid, long initialTime, long currentTime) {
        this.uuid = uuid;
        this.player = getPlayerFromUUID();
        this.color = "WHITE";
        this.style = "SEGMENTED_20";
        this.initialTime = initialTime;
        this.currentTime = currentTime;
        if (!TimedFly.getVersion().startsWith("v1_8")) this.bar = new Bossbars("", "white", "SEGMENTED_20");
        this.running = false;
    }

    public BossBarManager(UUID uuid, long initialTime, long currentTime, String color, String style) {
        this.uuid = uuid;
        this.player = getPlayerFromUUID();
        this.color = color;
        this.style = style;
        this.initialTime = initialTime;
        this.currentTime = currentTime;
        if (!TimedFly.getVersion().startsWith("v1_8")) this.bar = new Bossbars(title, color, style);
        this.running = false;
    }

    public void show() {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled() || isRunning()) return;
        setRunning(true);

        setBarColor(getColor());
        setBarStyle(getStyle());
        setBarProgress(initialTime);

        addPlayer(player);
    }

    public void hide() {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled() || !isRunning()) return;
        setRunning(false);
        setBarProgress(1, 1);
        removeBar(player);
    }

    public String getTitle() {
        return title;
    }

    public BossBarManager setTitle(String title) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return this;
        this.title = title;
        bar.setTitle(title);
        return this;
    }

    public String getColor() {
        return color;
    }

    public BossBarManager setColor(String color) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return this;
        this.color = color;
        return this;
    }

    public String getStyle() {
        return style;
    }

    public BossBarManager setStyle(String style) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return this;
        this.style = style;
        return this;
    }

    private long getInitialTime() {
        return initialTime;
    }

    public BossBarManager setInitialTime(long initialTime) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return this;
        this.initialTime = initialTime;
        return this;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public BossBarManager setCurrentTime(long currentTime) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return this;
        this.currentTime = currentTime;
        return this;
    }

    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        this.running = running;
    }

    public BossBarManager createBar(String color, String style) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return this;
        this.bar = new Bossbars(Message.color(title), color, style);
        return this;
    }

    public BossBarManager addPlayer(Player player) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return this;
        if (!bar.getPlayers().contains(player) && bar != null) {
            bar.addPlayer(player);
        }
        return this;
    }

    public BossBarManager setBarProgress(double progress, double initialTime) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return this;
        double barProgress = initialTime / progress;

        if (Double.isInfinite(barProgress) || Double.isNaN(barProgress) || progress <= 0 || barProgress <= 0) {
            removeBar(player);
            return this;
        }

        double progr = 1 / barProgress;
        if (progr > 1) return this;

        bar.setProgress(progr);
        return this;
    }

    public BossBarManager setBarProgress(double progress) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return this;
        double barProgress = getInitialTime() / progress;

        if (Double.isInfinite(barProgress) || Double.isNaN(barProgress) || progress <= 0 || barProgress <= 0) {
            removeBar(player);
            return this;
        }

        double progr = 1 / barProgress;
        if (progr > 1) return this;

        bar.setProgress(progr);
        return this;
    }

    public boolean containsBar(Player player) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return false;
        return bar.getPlayers().contains(player);
    }

    public void removeBar(Player player) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return;
        if (containsBar(player)) bar.removePlayer(player);
    }

    public String getBarColor() {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return color;
        return bar.getColor().name();
    }

    public BossBarManager setBarColor(String color) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return this;
        bar.setColor(color.toUpperCase());
        return this;
    }

    public String getBarStyle() {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return style;
        return bar.getStyle().name().replace("_", "");
    }

    public BossBarManager setBarStyle(String style) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return this;
        bar.setStyle(style);
        return this;
    }

    public String getBarName() {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return title;
        return bar.getTitle();
    }

    public BossBarManager setBarName(String text) {
        if (TimedFly.getVersion().startsWith("v1_8") || !ConfigCache.isBossBarTimerEnabled()) return this;
        bar.setTitle(Message.color(text));
        return this;
    }

    public Player getPlayerFromUUID() {
        return Bukkit.getPlayer(uuid);
    }

    public Player getPlayer() {
        return player;
    }

    public BossBarManager setPlayer(Player player) {
        this.player = player;
        return this;
    }
}