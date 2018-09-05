package com.timedfly.NMS;

import org.bukkit.entity.Player;

public class UnSupported implements NMS {

    @Override
    public void sendActionbar(Player p, String message) {

    }

    @Override
    public void sendTitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
    }

    @Override
    public void sendSubtitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
    }

}
