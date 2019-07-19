package me.jackint0sh.timedfly.hooks.currencies;

import me.jackint0sh.timedfly.utilities.Currency;
import org.bukkit.entity.Player;

public class NoCurrency implements Currency {
    @Override
    public String name() {
        return "none";
    }

    @Override
    public boolean withdraw(Player player, int amount) {
        return true;
    }

    @Override
    public boolean deposit(Player player, int amount) {
        return true;
    }

    @Override
    public boolean has(Player player, int amount) {
        return true;
    }

    @Override
    public int balance(Player player) {
        return 0;
    }
}
