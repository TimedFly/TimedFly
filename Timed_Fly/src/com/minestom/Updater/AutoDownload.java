package com.minestom.Updater;

import com.minestom.TimedFly;
import com.minestom.Utilities.Utility;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.io.File;
import java.net.URL;

public class AutoDownload {

    private TimedFly plugin = TimedFly.getInstance();
    private Utility utility = new Utility(plugin);
    ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();

    public void autoDownload(int version, int id) {
        utility.message(sender, "&7Getting the latest version of &c&lTimedFly&7...");
        utility.message(sender, "&7Downloading...");

        String downloadURL = "https://api.spiget.org/v2/resources/" + id + "/download";
        File dir = new File(plugin.getDataFolder().getAbsolutePath());
        String pluginFolder = dir.getParentFile().getAbsolutePath() + "/TimedFly-v" + version + ".jar";

        try {
            FileUtils.copyURLToFile(new URL(downloadURL), new File(pluginFolder), 10000, 10000);
            utility.message(sender, "&7Downloaded the latest version of &c&lTimedFly&7!");
            utility.message(sender, "&7The changes will take effect in the next server restart!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


