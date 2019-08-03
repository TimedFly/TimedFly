package me.jackint0sh.timedfly.versions;

import org.bukkit.entity.Player;

public class Default extends ServerVersion {

    public Default() {
        serverVersion = this;
    }

    @Override
    public void sendActionBar(Player player, String text) {
    }

    @Override
    public void sendTitle(Player player, String title, String subtile) {
    }

    @Override
    public void sendTitle(Player player, String title, String subtile, int fadeIn, int stay, int fadeOut) {
    }
}
