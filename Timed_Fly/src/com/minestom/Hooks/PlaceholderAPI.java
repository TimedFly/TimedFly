package com.minestom.Hooks;

import com.minestom.ConfigurationFiles.LangFiles;
import com.minestom.TimedFly;
import com.minestom.Utilities.GUI.GUIListener;
import com.minestom.Managers.TimeFormat;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlaceholderAPI extends EZPlaceholderHook {

    private TimedFly plugin;
    private TimeFormat format = new TimeFormat();
    private LangFiles lang = LangFiles.getInstance();

    public PlaceholderAPI(TimedFly plugin) {
        super(plugin, "timedfly");
        this.plugin = plugin;
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        FileConfiguration lconfig = lang.getLang();
        if (identifier.equals("timeleft")) {
            if(GUIListener.flytime.containsKey(p.getUniqueId())){
                return format.format(GUIListener.flytime.get(p.getUniqueId()));
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
