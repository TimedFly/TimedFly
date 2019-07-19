package me.jackint0sh.timedfly.hooks.currencies;

import me.jackint0sh.timedfly.utilities.Currency;
import org.bukkit.entity.Player;

public class Exp implements Currency {

    @Override
    public String name() {
        return "exp";
    }

    @Override
    public boolean withdraw(Player player, int amount) {
        player.setExp(player.getExp() - amount);
        return true;
    }

    @Override
    public boolean deposit(Player player, int amount) {
        player.setExp(balance(player) + amount);
        return true;
    }

    @Override
    public boolean has(Player player, int amount) {
        return player.getExp() >= amount;
    }

    @Override
    public int balance(Player player) {
        return (int) player.getExp();
    }
}
