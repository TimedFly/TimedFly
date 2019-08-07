package me.jackint0sh.timedfly.hooks.currencies;

import me.jackint0sh.timedfly.hooks.Hooks;
import me.jackint0sh.timedfly.interfaces.Currency;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

public class PlayerPoints implements Currency {

    private PlayerPointsAPI playerPointsAPI = (PlayerPointsAPI) Hooks.getPlugin("PlayerPoints");

    @Override
    public String name() {
        return "playerpoints";
    }

    @Override
    public boolean withdraw(Player player, int amount) {
        return playerPointsAPI.take(player.getUniqueId(), amount);
    }

    @Override
    public boolean deposit(Player player, int amount) {
        return playerPointsAPI.give(player.getUniqueId(), amount);
    }

    @Override
    public boolean has(Player player, int amount) {
        return playerPointsAPI.look(player.getUniqueId()) >= amount;
    }

    @Override
    public int balance(Player player) {
        return playerPointsAPI.look(player.getUniqueId());
    }
}
