package me.jackint0sh.timedfly.hooks.currencies;

import me.jackint0sh.timedfly.interfaces.Currency;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class Vault implements Currency {

    private Economy economy;

    public Vault(Economy economy) {
        this.economy = economy;
    }

    @Override
    public String name() {
        return "vault";
    }

    @Override
    public boolean withdraw(Player player, int amount) {
        EconomyResponse r = this.economy.withdrawPlayer(player, amount);
        return r.transactionSuccess();
    }

    @Override
    public boolean deposit(Player player, int amount) {
        EconomyResponse r = this.economy.depositPlayer(player, amount);
        return r.transactionSuccess();
    }

    @Override
    public boolean has(Player player, int amount) {
        return this.economy.has(player, amount);
    }

    @Override
    public int balance(Player player) {
        return (int) this.economy.getBalance(player);
    }

}
