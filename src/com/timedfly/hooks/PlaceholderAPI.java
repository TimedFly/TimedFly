package com.timedfly.hooks;

import com.timedfly.TimedFly;
import com.timedfly.configurations.Languages;
import com.timedfly.managers.PlayerManager;
import com.timedfly.utilities.TimeFormat;
import com.timedfly.utilities.Utilities;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlaceholderAPI extends EZPlaceholderHook {

    private Languages languages;
    private Utilities utilities;

    public PlaceholderAPI(TimedFly plugin, Languages languages, Utilities utilities) {
        super(plugin, "timedfly");
        this.languages = languages;
        this.utilities = utilities;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return "You must be a player";

        FileConfiguration languageConfig = languages.getLanguageFile();
        PlayerManager playerManager = utilities.getPlayerManager(player.getUniqueId());


        switch (identifier) {
            case "timeleft_formatted":
                if (playerManager.getTimeLeft() < 0) return languageConfig.getString("Format.NoTimeLeft");
                return TimeFormat.formatLong(playerManager.getTimeLeft());
            case "timeleft_formatted_short":
                if (playerManager.getTimeLeft() < 0) return languageConfig.getString("Format.NoTimeLeft");
                return TimeFormat.formatShort(playerManager.getTimeLeft());
            case "timeleft_seconds":
                if (playerManager.getTimeLeft() < 0) return languageConfig.getString("Format.NoTimeLeft");
                return Integer.toString(playerManager.getTimeLeft());
            case "timeleft_milli":
                if (playerManager.getTimeLeft() < 0) return languageConfig.getString("Format.NoTimeLeft");
                return Integer.toString(playerManager.getTimeLeft() * 1000);
            case "players":
                return Integer.toString(utilities.getPlayers());
            case "players_timeleft":
                return TimeFormat.formatLong(utilities.getPlayersTimeLeft());
            case "players_timeleft_short":
                return TimeFormat.formatShort(utilities.getPlayersTimeLeft());
        }
        return null;
    }
}
