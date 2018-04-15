package com.timedfly.Managers;

import com.timedfly.ConfigurationFiles.ConfigCache;
import com.timedfly.Hooks.TokensManager;
import com.timedfly.Utilities.Utility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class RemoveMoney {

    private Utility utility;
    private TokensManager tokensManager;
    private Economy economy;
    private ConfigCache configCache;

    public RemoveMoney(Utility utility, TokensManager tokensManager, Economy economy, ConfigCache configCache) {
        this.utility = utility;
        this.tokensManager = tokensManager;
        this.economy = economy;
        this.configCache = configCache;
    }

    public boolean withdraw(Player player, Integer price, Integer time) {
        if (configCache.isUseTokenManager()) {
            if (tokensManager.getTokens(player) >= price) {
                tokensManager.removeTokens(player, price);
            } else {
                player.closeInventory();
                utility.message(player, MessageManager.NOMONEY.toString().replace("%price%", price + "")
                        .replace("%time%", time + ""));
                return true;
            }
        }
        if (configCache.isUseVault()) {
            if (economy.has(player, price)) {
                economy.withdrawPlayer(player, price);
            } else {
                player.closeInventory();
                utility.message(player, MessageManager.NOMONEY.toString().replace("%price%", price + "")
                        .replace("%time%", time + ""));
                return true;
            }
        }
        if (configCache.isUseLevelsCurrency()) {
            if (player.getLevel() >= price) {
                player.setLevel(player.getLevel() - price);
            } else {
                player.closeInventory();
                utility.message(player, MessageManager.NotEnoughLevels.toString().replace("%levels_needed%", price + "")
                        .replace("%time%", time + ""));
                return true;
            }
        }
        if (configCache.isUseExpCurrency()) {
            if (player.getTotalExperience() >= price) {
                player.setTotalExperience(player.getTotalExperience() - price);
            } else {
                player.closeInventory();
                utility.message(player, MessageManager.NotExpLevels.toString().replace("%exp_needed%", price + "")
                        .replace("%time%", time + ""));
                return true;
            }
        }
        return false;
    }

    public boolean noCurrencyFound(Player player, FileConfiguration config) {
        if (!configCache.isUseLevelsCurrency() && !configCache.isUseVault() && !configCache.isUseTokenManager()
                && !configCache.isUseExpCurrency()) {
            player.closeInventory();
            utility.message(player, config.getString("Other.NoCurrencyFound"));
            return true;
        }
        return false;
    }

}
