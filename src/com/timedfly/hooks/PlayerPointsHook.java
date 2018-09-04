package com.timedfly.hooks;

import com.timedfly.TimedFly;
import com.timedfly.managers.HooksManager;
import org.bukkit.Bukkit;

import java.text.NumberFormat;
import java.util.UUID;

public class PlayerPointsHook {

    public String getPoints(UUID uuid) {
        if (!HooksManager.isPlayerPointsEnabled()) return "PlayerPoints Not Found";

        final int points = getPlayerPointsAPI().look(uuid);
        return NumberFormat.getIntegerInstance().format(points);
    }

    public boolean hasPoints(UUID uuid, int points) {
        if (!HooksManager.isPlayerPointsEnabled()) return false;
        return getPlayerPointsAPI().look(uuid) >= points;
    }

    public void addPoints(UUID uuid, int points) {
        if (!HooksManager.isPlayerPointsEnabled()) return;
        getPlayerPointsAPI().give(uuid, points);
    }

    public void removePoints(UUID uuid, int points) {
        if (!HooksManager.isPlayerPointsEnabled()) return;
        getPlayerPointsAPI().take(uuid, points);
    }

    private org.black_ixx.playerpoints.PlayerPointsAPI getPlayerPointsAPI() {
        org.black_ixx.playerpoints.PlayerPoints playerPoints = (org.black_ixx.playerpoints.PlayerPoints)
                Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints");
        return playerPoints.getAPI();
    }
}
