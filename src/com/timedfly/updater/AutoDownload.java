package com.timedfly.updater;

import com.timedfly.TimedFly;
import com.timedfly.utilities.Message;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.io.File;
import java.net.URL;

public class AutoDownload {

    private TimedFly plugin;

    public AutoDownload(TimedFly plugin) {
        this.plugin = plugin;
    }

    public void autoDownload(int id) {
        ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();
        Message.sendMessage(sender, "&7Getting the latest version of &c&lTimedFly&7...");
        Message.sendMessage(sender, "&7Downloading...");

        String downloadURL = "https://api.spiget.org/v2/resources/" + id + "/download";
        File dir = new File(plugin.getDataFolder().getAbsolutePath());
        String pluginFolder = dir.getParentFile().getAbsolutePath() + "/TimedFly.jar";

        try {
            FileUtils.copyURLToFile(new URL(downloadURL), new File(pluginFolder), 10000, 10000);
            Message.sendMessage(sender, "&7Downloaded the latest version of &c&lTimedFly&7!");
            Message.sendMessage(sender, "&7The changes will take effect in the next server restart!");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


