package com.timedfly.hooks;

import com.timedfly.managers.HooksManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

public class TokenManager {

    public String tokens(Player player) {
        if (!HooksManager.isTokenManagerEnabled()) return "TokenManager Not Found";

        final me.realized.tokenmanager.api.TokenManager tokenManager = getTokenManager();
        final double tokens = (double) tokenManager.getTokens(player).orElse(0);

        return NumberFormat.getIntegerInstance().format(tokens);
    }

    public long getTokens(Player player) {
        if (!HooksManager.isTokenManagerEnabled()) return 0;

        final me.realized.tokenmanager.api.TokenManager tokenManager = getTokenManager();

        return tokenManager.getTokens(player).orElse(0);
    }

    public void addTokens(Player player, int price) {
        if (!HooksManager.isTokenManagerEnabled()) return;

        final me.realized.tokenmanager.api.TokenManager tokenManager = getTokenManager();

        tokenManager.addTokens(player.getUniqueId().toString(), price);
    }

    public void removeTokens(Player player, int price) {
        if (!HooksManager.isTokenManagerEnabled()) return;

        final me.realized.tokenmanager.api.TokenManager tokenManager = getTokenManager();

        tokenManager.removeTokens(player.getUniqueId().toString(), price);
    }

    private me.realized.tokenmanager.api.TokenManager getTokenManager() {
        return (me.realized.tokenmanager.api.TokenManager) Bukkit.getPluginManager().getPlugin("TokenManager");
    }

}
