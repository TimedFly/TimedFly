package com.timedfly.Hooks;

import com.timedfly.ConfigurationFiles.LangFiles;
import com.timedfly.Managers.TimeFormat;
import com.timedfly.TimedFly;
import com.timedfly.Listeners.GUIListener;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlaceholderAPI extends EZPlaceholderHook {

    private LangFiles lang = LangFiles.getInstance();

    public PlaceholderAPI(TimedFly plugin) {
        super(plugin, "timedfly");
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        FileConfiguration lconfig = lang.getLang();
        if (identifier.equals("timeleft")) {
            if (GUIListener.flytime.containsKey(p.getUniqueId())) {
                return TimeFormat.format(GUIListener.flytime.get(p.getUniqueId()));
            } else {
                return lconfig.getString("Format.NoTimeLeft");
            }
        }
        if (p == null) {
            return "You need to be a player";
        }
        return null;
    }
}
