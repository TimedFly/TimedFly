package me.jackint0sh.timedfly.hooks.currencies;

import me.jackint0sh.timedfly.interfaces.Currency;
import org.bukkit.entity.Player;

public class Levels implements Currency {

    @Override
    public String name() {
        return "levels";
    }

    @Override
    public boolean withdraw(Player player, int amount) {
        player.setLevel(player.getLevel() - amount);
        return true;
    }

    @Override
    public boolean deposit(Player player, int amount) {
        player.setLevel(player.getLevel() + amount);
        return true;
    }

    @Override
    public boolean has(Player player, int amount) {
        return balance(player) >= amount;
    }

    @Override
    public int balance(Player player) {
        return player.getLevel();
    }
}
