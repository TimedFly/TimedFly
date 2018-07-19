package com.timedfly.utilities;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;

public class Bossbars {

    private BossBar bar;

    public Bossbars(String title, String barColor, String barStyle) {
        this.bar = Bukkit.createBossBar(title, BarColor.valueOf(barColor.toUpperCase()), BarStyle.valueOf(barStyle.toUpperCase()));
    }

    public String getTitle() {
        return bar.getTitle();
    }

    public Bossbars setTitle(String title) {
        bar.setTitle(title);
        return this;
    }

    public List<Player> getPlayers() {
        return bar.getPlayers();
    }

    public Bossbars addPlayer(Player player) {
        bar.addPlayer(player);
        return this;
    }

    public Bossbars removePlayer(Player player) {
        bar.removePlayer(player);
        return this;
    }

    public double getProgress() {
        return bar.getProgress();
    }

    public Bossbars setProgress(double progress) {
        bar.setProgress(progress);
        return this;
    }

    public BarColor getColor() {
        return bar.getColor();
    }

    public Bossbars setColor(String color) {
        bar.setColor(BarColor.valueOf(color.toUpperCase()));
        return this;
    }

    public BarStyle getStyle() {
        return bar.getStyle();
    }

    public Bossbars setStyle(String style) {
        bar.setStyle(BarStyle.valueOf(style.toUpperCase()));
        return this;
    }
}
