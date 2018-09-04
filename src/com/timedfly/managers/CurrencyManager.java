package com.timedfly.managers;

import com.timedfly.configurations.ConfigCache;
import com.timedfly.configurations.Languages;
import com.timedfly.hooks.PlayerPointsHook;
import com.timedfly.hooks.TokenManager;
import com.timedfly.hooks.Vault;
import com.timedfly.utilities.Message;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CurrencyManager {

    private PlayerPointsHook playerPointsHook;
    private TokenManager tokensManager;
    private Languages languages;
    private Vault vault;

    public CurrencyManager(TokenManager tokensManager, PlayerPointsHook playerPointsHook, Vault vault, Languages languages) {
        this.tokensManager = tokensManager;
        this.playerPointsHook = playerPointsHook;
        this.vault = vault;
        this.languages = languages;
    }

    public boolean withdraw(Player player, Integer price, Integer time, RefundManager refundManager) {
        if (ConfigCache.isUseTokenManager() && HooksManager.isTokenManagerEnabled()) {
            if (tokensManager.getTokens(player) >= price) {
                tokensManager.removeTokens(player, price);
                refundManager.addPaymentType(RefundManager.PaymentType.TOKENS_MANAGER);
                player.closeInventory();
                Message.sendNoMoney(player, languages.getLanguageFile(), price, time);
                return false;
            }
        }
        if (ConfigCache.isUseVault() && HooksManager.isVaultEnabled()) {
            if (vault.hasMoney(player, price)) {
                vault.removeMoney(player, price);
                refundManager.addPaymentType(RefundManager.PaymentType.VAULT);
            } else {
                player.closeInventory();
                Message.sendNoMoney(player, languages.getLanguageFile(), price, time);
                return false;
            }
        }
        if (ConfigCache.isUsePlayerPoints() && HooksManager.isPlayerPointsEnabled()) {
            if (playerPointsHook.hasPoints(player.getUniqueId(), price)) {
                playerPointsHook.removePoints(player.getUniqueId(), price);
                refundManager.addPaymentType(RefundManager.PaymentType.PLAYER_POINTS);
            } else {
                player.closeInventory();
                Message.sendNoMoney(player, languages.getLanguageFile(), price, time);
                return false;
            }
        }
        if (ConfigCache.isUseLevelsCurrency()) {
            if (player.getLevel() >= price) {
                player.setLevel(player.getLevel() - price);
                refundManager.addPaymentType(RefundManager.PaymentType.LEVELS);
            } else {
                player.closeInventory();
                Message.sendNoMoney(player, languages.getLanguageFile(), price, time);
                return false;
            }
        }
        if (ConfigCache.isUseExpCurrency()) {
            if (player.getTotalExperience() >= price) {
                player.setTotalExperience(player.getTotalExperience() - price);
                refundManager.addPaymentType(RefundManager.PaymentType.EXP);
            } else {
                player.closeInventory();
                Message.sendNoMoney(player, languages.getLanguageFile(), price, time);
                return false;
            }
        }
        refundManager.setAmountPayed(price).setTimeBought(time * 60);
        return true;
    }

    public boolean noCurrencyFound(Player player, FileConfiguration config) {
        if (!ConfigCache.isUseLevelsCurrency() && !ConfigCache.isUseVault() && !ConfigCache.isUseTokenManager()
                && !ConfigCache.isUseExpCurrency() && !ConfigCache.isUsePlayerPoints()) {
            player.closeInventory();
            Message.sendMessage(player, config.getString("Other.NoCurrencyFound"));
            return false;
        }
        return true;
    }

}
