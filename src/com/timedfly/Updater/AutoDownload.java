package com.timedfly.Updater;

import com.timedfly.TimedFly;
import com.timedfly.Utilities.Utility;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.io.File;
import java.net.URL;

public class AutoDownload {

    private TimedFly plugin;
    private Utility utility;

    public AutoDownload(TimedFly plugin) {
        this.plugin = plugin;
        this.utility = plugin.getUtility();
    }

    public void autoDownload(int id) {
        ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();
        utility.message(sender, "&7Getting the latest version of &c&lTimedFly&7...");
        utility.message(sender, "&7Downloading...");

        String downloadURL = "https://api.spiget.org/v2/resources/" + id + "/download";
        File dir = new File(plugin.getDataFolder().getAbsolutePath());
        String pluginFolder = dir.getParentFile().getAbsolutePath() + "/TimedFly.jar";

        try {
            FileUtils.copyURLToFile(new URL(downloadURL), new File(pluginFolder), 10000, 10000);
            utility.message(sender, "&7Downloaded the latest version of &c&lTimedFly&7!");
            utility.message(sender, "&7The changes will take effect in the next server restart!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


