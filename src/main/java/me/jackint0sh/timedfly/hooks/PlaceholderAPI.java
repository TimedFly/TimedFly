package me.jackint0sh.timedfly.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.jackint0sh.timedfly.managers.PlayerManager;
import me.jackint0sh.timedfly.utilities.TimeParser;
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

        PlayerManager playerManager = PlayerManager.getCachedPlayer(player.getUniqueId());
        if (playerManager != null) {
            long timeLeft = playerManager.getTimeLeft();
            switch (identifier) {
                case "timeleft":
                case "timeleft_long":
                    if (timeLeft <= 0) return "No Time Left";
                    return TimeParser.toReadableString(timeLeft);
                case "timeleft_short":
                    if (timeLeft <= 0) return "No Time Left";
                    return TimeParser.toReadableString(timeLeft, false);
                case "timeleft_ms":
                    return timeLeft + "";
                case "timeleft_cut":
                case "timeleft_cut_long":
                    if (timeLeft <= 0) return "No Time Left";
                    return TimeParser.toReadableString(timeLeft, true, true);
                case "timeleft_cut_short":
                    if (timeLeft <= 0) return "No Time Left";
                    return TimeParser.toReadableString(timeLeft, false, true);
                default:
                    return null;
            }
        }

        return "ERROR";
    }
}
