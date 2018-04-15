package com.timedfly.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarManager {

    private long initialTime;
    private String title;
    private String color;
    private String style;
    private long currentTime;
    private boolean running;
    private Player player;
    private BossBar bar;

    public BossBarManager(Player player, String title, String color, String style, long initialTime) {
        this.player = player;
        this.title = title;
        this.color = color;
        this.style = style;
        this.initialTime = initialTime;
        this.currentTime = initialTime;
        this.bar = Bukkit.createBossBar(title, BarColor.valueOf(color), BarStyle.valueOf(style));
        this.running = false;
    }

    public void show() {
        setRunning(true);

        setBarColor(getColor());
        setBarStyle(getStyle());
        setBarProgress(initialTime);

        addPlayer(player);
    }

    public void hide() {
        setRunning(false);
        setBarProgress(1, 1);
        removeBar(player);
    }

    public String getTitle() {
        return title;
    }

    public BossBarManager setTitle(String title) {
        this.title = title;
        bar.setTitle(title);
        return this;
    }

    public String getColor() {
        return color;
    }

    public BossBarManager setColor(String color) {
        this.color = color;
        return this;
    }

    public String getStyle() {
        return style;
    }

    public BossBarManager setStyle(String style) {
        this.style = style;
        return this;
    }

    private long getInitialTime() {
        return initialTime;
    }

    public BossBarManager setInitialTime(long initialTime) {
        this.initialTime = initialTime;
        return this;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public BossBarManager setCurrentTime(long currentTime) {
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
        bar = Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', title),
                BarColor.valueOf(color.toUpperCase()), BarStyle.valueOf(style.toUpperCase()));
        return this;
    }

    public BossBarManager addPlayer(Player player) {
        if (!bar.getPlayers().contains(player) && bar != null) {
            bar.addPlayer(player);
        }
        return this;
    }

    public BossBarManager setBarProgress(double progress, double initialTime) {
        double barProgress = initialTime / progress;
        bar.setProgress(1 / barProgress);
        return this;
    }

    public BossBarManager setBarProgress(double progress) {
        double barProgress = initialTime / progress;
        bar.setProgress(1 / barProgress);
        return this;
    }

    public boolean containsBar(Player player) {
        return bar.getPlayers().contains(player);
    }

    public void removeBar(Player player) {
        bar.removePlayer(player);
    }

    public String getBarColor() {
        return bar.getColor().name();
    }

    public BossBarManager setBarColor(String color) {
        bar.setColor(BarColor.valueOf(color.toUpperCase()));
        return this;
    }

    public String getBarStyle() {
        return bar.getStyle().name().replace("_", "");
    }

    public BossBarManager setBarStyle(String style) {
        bar.setStyle(BarStyle.valueOf(style.toUpperCase()));
        return this;
    }

    public String getBarName() {
        return bar.getTitle();
    }

    public BossBarManager setBarName(String text) {
        bar.setTitle(ChatColor.translateAlternateColorCodes('&', text));
        return this;
    }
}