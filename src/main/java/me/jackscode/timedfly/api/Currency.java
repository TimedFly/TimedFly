package me.jackscode.timedfly.api;

import org.bukkit.entity.Player;

public interface Currency {
    String name();

    boolean withdraw(Player player, int amount);

    boolean deposit(Player player, int amount);

    boolean has(Player player, int amount);

    int balance(Player player);
}
