package me.jackint0sh.timedfly.hooks.currencies;

import me.jackint0sh.timedfly.hooks.Hooks;
import me.jackint0sh.timedfly.interfaces.Currency;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

public class HPlayerPoints implements Currency {

    private PlayerPoints playerPoints = (PlayerPoints) Hooks.getPlugin("PlayerPoints");
    private PlayerPointsAPI api = playerPoints.getAPI();

    @Override
    public String name() {
        return "playerpoints";
    }

    @Override
    public boolean withdraw(Player player, int amount) {
        return api.take(player.getUniqueId(), amount);
    }

    @Override
    public boolean deposit(Player player, int amount) {
        return api.give(player.getUniqueId(), amount);
    }

    @Override
    public boolean has(Player player, int amount) {
        return api.look(player.getUniqueId()) >= amount;
    }

    @Override
    public int balance(Player player) {
        return api.look(player.getUniqueId());
    }
}
