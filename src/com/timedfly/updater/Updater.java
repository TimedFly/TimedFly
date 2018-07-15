package com.timedfly.updater;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.timedfly.TimedFly;
import com.timedfly.configurations.ConfigCache;
import com.timedfly.utilities.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {

    private final TimedFly plugin;
    private int resourceId;
    private String newVersion;
    private boolean checkForUpdates;
    private final AutoDownload autoDownload;

    public Updater(TimedFly plugin) {
        this.plugin = plugin;
        this.autoDownload = new AutoDownload(plugin);
    }

    public void sendUpdateMessage() {
        fetchUpdates();
        if (!this.checkForUpdates) return;

        int newVersion = Integer.parseInt(this.newVersion.replaceAll("[^\\d]", ""));
        int currentVersion = Integer.parseInt(getCurrentVersion().replaceAll("[^\\d]", ""));

        Message.sendMessage(Bukkit.getConsoleSender(), "&7Looking for updates...");

        if (newVersion > currentVersion) {
            Message.sendMessage(Bukkit.getConsoleSender(), "&7There is a new update available.");
            Message.sendMessage(Bukkit.getConsoleSender(), "&7Current version " + getCurrentVersion());
            Message.sendMessage(Bukkit.getConsoleSender(), "&7Newest version " + this.newVersion);
            Message.sendMessage(Bukkit.getConsoleSender(), "&7Download the new version here: https://www.spigotmc.org/resources/" + resourceId);

            if (ConfigCache.isAutoDownload()) autoDownload.autoDownload(resourceId);
        } else {
            Message.sendMessage(Bukkit.getConsoleSender(), "&7Plugin is up-to-date");
        }
    }

    public void sendUpdateMessage(CommandSender sender) {
        fetchUpdates();
        if (!this.checkForUpdates) return;

        int newVersion = Integer.parseInt(this.newVersion.replaceAll("[^\\d]", ""));
        int currentVersion = Integer.parseInt(getCurrentVersion().replaceAll("[^\\d]", ""));

        if (newVersion > currentVersion) {
            String[] messages = {"There is a new update available.", "Current version: &c" + getCurrentVersion(),
                    "Newest version: &c" + this.newVersion, "Download the new version here:",
                    "https://www.spigotmc.org/resources/" + resourceId};
            for (String message : messages) Message.sendMessage(sender, message);
        }
    }

    public void sendUpdateMessage(Player player) {
        fetchUpdates();
        if (!this.checkForUpdates) return;

        int newVersion = Integer.parseInt(this.newVersion.replaceAll("[^\\d]", ""));
        int currentVersion = Integer.parseInt(getCurrentVersion().replaceAll("[^\\d]", ""));

        if (newVersion > currentVersion) {
            String[] message = {"There is a new update available.", "Current version: &c" + getCurrentVersion(),
                    "Newest version: &c" + newVersion, "Download the new version here:",
                    "https://www.spigotmc.org/resources/" + resourceId};

            for (String string : message) Message.sendMessage(player, string);
        }
    }

    private void fetchUpdates() {
        if (!ConfigCache.isCheckForUpdates()) return;
        try {
            URL url = new URL("https://jackbot.pw/spigot/plugins.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            JsonElement element = new JsonParser().parse(reader).getAsJsonObject().get(plugin.getName());

            String newVersion = element.getAsJsonObject().get("version").getAsString();
            int resourceId = element.getAsJsonObject().get("resource-id").getAsInt();
            boolean checkForUpdates = element.getAsJsonObject().get("check-for-updates").getAsBoolean();

            this.newVersion = newVersion;
            this.resourceId = resourceId;
            this.checkForUpdates = checkForUpdates;

        } catch (IOException e) {
            Message.sendMessage(Bukkit.getConsoleSender(), "Error while checking for updates.");
            Message.sendMessage(Bukkit.getConsoleSender(), e.getMessage());
        }
    }

    private String getCurrentVersion() {
        return plugin.getDescription().getVersion();
    }
}