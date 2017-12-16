package com.minestom.Utilities;

import com.minestom.TimedFly;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class Utility {
    private TimedFly plugin;
    public Utility(TimedFly plugin){
        this.plugin = plugin;
    }

    public boolean isWorldEnabled(Player player) {
        FileConfiguration configuration = plugin.getConfig();
        if(configuration.getString("World-List.Type").equalsIgnoreCase("enabled")) {
            List<String> ENABLED_WORLDS = configuration.getStringList("World-List.Worlds");
            for (String worlds : ENABLED_WORLDS) {
                World world = Bukkit.getWorld(worlds);
                if (player.getWorld() == world) {
                    return true;
                }
            }
        }
        if(configuration.getString("World-List.Type").equalsIgnoreCase("disabled")) {
            List<String> ENABLED_WORLDS = configuration.getStringList("World-List.Worlds");
            for (String worlds : ENABLED_WORLDS) {
                World world = Bukkit.getWorld(worlds);
                if (player.getWorld() == world) {
                    return false;
                }
            }
        }
        if(configuration.getString("World-List.Type").equalsIgnoreCase("all")) {
            return true;
        }
        return false;
    }

    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void message(Player sender, String string) {
        String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Prefix"));
        if (prefix.equalsIgnoreCase("none")) {
            sender.sendMessage(color(string));
            return;
        }
        sender.sendMessage(prefix + color(string));
    }

    public void message(CommandSender sender, String string) {
        String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Prefix"));
        if (prefix.equalsIgnoreCase("none")) {
            sender.sendMessage(color(string));
        } else {
            sender.sendMessage(prefix + color(string));
        }
    }

    public boolean isTokenManagerEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("TokenManager");
    }

    public boolean isPapiEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }
}
