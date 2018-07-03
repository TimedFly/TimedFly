package com.timedfly.managers;

import com.timedfly.configurations.ConfigCache;
import com.timedfly.configurations.Languages;
import com.timedfly.hooks.TokenManager;
import com.timedfly.utilities.Message;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CurrencyManager {

    private TokenManager tokensManager;
    private Languages languages;
    private Economy economy;

    public CurrencyManager(TokenManager tokensManager, Economy economy, Languages languages) {
        this.tokensManager = tokensManager;
        this.economy = economy;
        this.languages = languages;
    }

    public boolean withdraw(Player player, Integer price, Integer time) {
        if (ConfigCache.isUseTokenManager() && HooksManager.isTokenManagerEnabled()) {
            if (tokensManager.getTokens(player) >= price) {
                tokensManager.removeTokens(player, price);
            } else {
                player.closeInventory();
                Message.sendNoMoney(player, languages.getLanguageFile(), price, time);
                return false;
            }
        }
        if (ConfigCache.isUseVault()) {
            if (economy.has(player, price)) {
                economy.withdrawPlayer(player, price);
            } else {
                player.closeInventory();
                Message.sendNoMoney(player, languages.getLanguageFile(), price, time);
                return false;
            }
        }
        if (ConfigCache.isUseLevelsCurrency()) {
            if (player.getLevel() >= price) {
                player.setLevel(player.getLevel() - price);
            } else {
                player.closeInventory();
                Message.sendNoMoney(player, languages.getLanguageFile(), price, time);
                return false;
            }
        }
        if (ConfigCache.isUseExpCurrency()) {
            if (player.getTotalExperience() >= price) {
                player.setTotalExperience(player.getTotalExperience() - price);
            } else {
                player.closeInventory();
                Message.sendNoMoney(player, languages.getLanguageFile(), price, time);
                return false;
            }
        }
        return true;
    }

    public boolean noCurrencyFound(Player player, FileConfiguration config) {
        if (!ConfigCache.isUseLevelsCurrency() && !ConfigCache.isUseVault() && !ConfigCache.isUseTokenManager()
                && !ConfigCache.isUseExpCurrency()) {
            player.closeInventory();
            Message.sendMessage(player, config.getString("Other.NoCurrencyFound"));
            return false;
        }
        return true;
    }

}
