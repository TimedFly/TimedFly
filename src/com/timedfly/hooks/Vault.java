package com.timedfly.hooks;

import com.timedfly.managers.HooksManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.NumberFormat;

public class Vault {

    public String getMoney(Player player) {
        if (!HooksManager.isVaultEnabled() || getVault() == null) return "Vault Not Found";

        final double points = getVault().getBalance(player);
        return NumberFormat.getIntegerInstance().format(points);
    }

    public boolean hasMoney(Player player, double money) {
        if (!HooksManager.isVaultEnabled() || getVault() == null) return false;

        return getVault().has(player, money);
    }

    public void addMoney(Player player, double money) {
        if (!HooksManager.isVaultEnabled() || getVault() == null) return;

        getVault().depositPlayer(player, money);
    }

    public void removeMoney(Player player, double money) {
        if (!HooksManager.isVaultEnabled() || getVault() == null) return;

        getVault().withdrawPlayer(player, money);
    }

    private net.milkbowl.vault.economy.Economy getVault() {

        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp =
                Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) return null;

        return rsp.getProvider();
    }

}
