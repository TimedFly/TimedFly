package com.timedfly.Hooks;

import me.realized.tm.api.TMAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

public class TokensManager {

    public String tokens(Player player) {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("TokenManager")) return "Tokens Manager Not Found";
        return NumberFormat.getIntegerInstance().format(Double.valueOf(TMAPI.getTokens(player)));
    }

    public long getTokens(Player player) {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("TokenManager")) return 0;
        return TMAPI.getTokens(player);
    }

    public void removeTokens(Player player, int price) {
        if (!Bukkit.getServer().getPluginManager().isPluginEnabled("TokenManager")) return;
        TMAPI.removeTokens(player, price);
    }
}
