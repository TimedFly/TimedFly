package com.minestom.Utilities;

import com.minestom.TimedFly;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarManager {

    private TimedFly plugin;

    public BossBarManager(TimedFly plugin) {
        this.plugin = plugin;
    }

    private BossBar bar;

    public void addPlayer(Player player) {
        if (bar == null) {
            bar = Bukkit.createBossBar(null,
                    BarColor.valueOf(plugin.getConfig().getString("BossBarTimer.Color").toUpperCase()),
                    BarStyle.valueOf(plugin.getConfig().getString("BossBarTimer.Style").toUpperCase()));
        }
        if (!plugin.getConfig().getBoolean("BossBarTimer.Enabled")) {
            return;
        }
        if (!bar.getPlayers().contains(player) && bar != null) {
            bar.addPlayer(player);
        } else {
            removeBar(player);
        }
    }


    public void setBarProgress(double progress, double initialTime) {
        if (!plugin.getConfig().getBoolean("BossBarTimer.Enabled") && bar == null) {
            return;
        }
        double barProgress = initialTime / progress;
        bar.setProgress(1 / barProgress);
    }

    public void removeBar(Player player) {
        if (!plugin.getConfig().getBoolean("BossBarTimer.Enabled") && bar == null) {
            return;
        }
        if (bar.getPlayers().contains(player))
            bar.removePlayer(player);
    }

    public void setBarName(String text) {
        if (!plugin.getConfig().getBoolean("BossBarTimer.Enabled")) {
            return;
        }
        bar.setTitle(text);
    }
}
