package com.minestom.Managers;

import com.minestom.TimedFly;
import me.realized.tm.api.TMAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.NumberFormat;

public class DependenciesManager {

    private TimedFly plugin;

    public DependenciesManager(TimedFly plugin){
        this.plugin = plugin;
    }

    public String tokens(Player player){
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("TokenManager")) {
            return NumberFormat.getIntegerInstance().format(Double.valueOf(TMAPI.getTokens(player)));
        } else {
            return "§cTokenManager not found";
        }
    }
    public long getTokens(Player player){
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("TokenManager")) {
            return TMAPI.getTokens(player);
        } else {
            return 0;
        }
    }
    public void removeTokens(Player player, int price){
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("TokenManager")) {
            TMAPI.removeTokens(player, price);
        }
    }
}
