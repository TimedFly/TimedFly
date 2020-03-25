package me.jackint0sh.timedfly.hooks.currencies;

import me.jackint0sh.timedfly.hooks.Hooks;
import me.jackint0sh.timedfly.interfaces.Currency;
import me.realized.tokenmanager.api.TokenManager;
import org.bukkit.entity.Player;

public class HTokenManager implements Currency {

    private TokenManager tokenManager = (TokenManager) Hooks.getPlugin("TokenManager");

    @Override
    public String name() {
        return "tokenmanager";
    }

    @Override
    public boolean withdraw(Player player, int amount) {
        return tokenManager.removeTokens(player, amount);
    }

    @Override
    public boolean deposit(Player player, int amount) {
        return tokenManager.addTokens(player, amount);
    }

    @Override
    public boolean has(Player player, int amount) {
        return tokenManager.getTokens(player).orElse(0) >= amount;
    }

    @Override
    public int balance(Player player) {
        return (int) tokenManager.getTokens(player).orElse(0);
    }
}
