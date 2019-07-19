package me.jackint0sh.timedfly.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.jackint0sh.timedfly.managers.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlaceholderAPI extends PlaceholderExpansion {


    private Plugin plugin;

    PlaceholderAPI(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return "timedfly";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        if (player == null) return "Only players allowed!";

        if (identifier.equals("timeleft")) {
            PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
            if (playerManager != null) {
                return playerManager.getTimeLeft() + ""; // TODO: Convert ticks to readable time string.
            }
        }
        // TODO: Rest of the identifiers

        return "ERROR";
    }
}
