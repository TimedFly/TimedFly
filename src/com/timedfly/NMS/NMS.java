package com.timedfly.NMS;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface NMS {

    void sendActionbar(Player player, String message);

    void sendTitle(Player player, String title, int fadeIn, int stay, int fadeOut);

    void sendSubtitle(Player player, String subtitle, int fadeIn, int stay, int fadeOut);


}
