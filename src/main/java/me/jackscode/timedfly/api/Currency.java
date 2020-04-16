package me.jackscode.timedfly.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface Currency {
    String name();

    boolean withdraw(@NotNull Player player, int amount);

    boolean deposit(@NotNull Player player, int amount);

    boolean has(@NotNull Player player, int amount);

    int balance(@NotNull Player player);
}
