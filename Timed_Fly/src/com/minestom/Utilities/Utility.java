package com.minestom.Utilities;

import com.minestom.TimedFly;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class Utility {

    private TimedFly plugin;
    public Utility(TimedFly plugin) {
        this.plugin = plugin;
    }

    public boolean isWorldEnabled(Player player, World playerWorld) {
        FileConfiguration configuration = plugin.getConfig();
        if (configuration.getString("World-List.Type").equalsIgnoreCase("enabled")) {
            List<String> ENABLED_WORLDS = configuration.getStringList("World-List.Worlds");
            for (String worlds : ENABLED_WORLDS) {
                World world = Bukkit.getWorld(worlds);
                if (playerWorld == world) {
                    return true;
                }
            }
        }
        if (configuration.getString("World-List.Type").equalsIgnoreCase("disabled")) {
            List<String> ENABLED_WORLDS = configuration.getStringList("World-List.Worlds");
            for (String worlds : ENABLED_WORLDS) {
                World world = Bukkit.getWorld(worlds);
                if (playerWorld == world) {
                    return false;
                }
            }
        }
        if (configuration.getString("World-List.Type").equalsIgnoreCase("all")) {
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

    public void clickEvent(Player player, List<String> string) {
        for (String click : string) {
            if (click.contains("[message]")) {
                if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                    String msgNoPAPI = ChatColor.translateAlternateColorCodes('&', click.replace("[message] ", ""));
                    String msgPAPI = PlaceholderAPI.setPlaceholders(player, msgNoPAPI);
                    player.sendMessage(msgPAPI);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', click.replace("[message] ", "")));
                }
            }
            if (click.contains("[player]")) {
                Bukkit.dispatchCommand(player, click.replace("[player] ", "").replace("%player%", player.getName()));
            }
            if (click.contains("[console]")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        click.replace("[console] ", "").replace("%player%", player.getName()));

            }
            if (click.contains("[sound]")) {
                if (plugin.getConfig().getBoolean("Sounds.Enabled")) {
                    player.playSound(player.getLocation(), Sound.valueOf(click.replace("[sound] ", "")), 100, 1);
                }
            }
            if (click.contains("[close]")) {
                player.closeInventory();
            }
        }
    }

    public boolean isTokenManagerEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("TokenManager");
    }

    public boolean isPapiEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public boolean isXpCurrencyEnabled() {
        FileConfiguration configuration = plugin.getConfig();
        return configuration.getBoolean("UseLevelsCurrency");
    }
}
