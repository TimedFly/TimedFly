package com.timedfly.Updater;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.timedfly.TimedFly;
import com.timedfly.Utilities.Utility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Update {

    private final TimedFly plugin;
    private final Logger logger;
    private int resourceId;
    private String newVersion;
    private boolean checkForUpdates;
    private final Utility utility;
    private final AutoDownload autoDownload;

    public Update(TimedFly plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.utility = plugin.getUtility();
        this.autoDownload = new AutoDownload(plugin);
    }

    public void sendUpdateMessage() {
        fetchUpdates();
        if (!this.checkForUpdates) return;

        int newVersion = Integer.parseInt(this.newVersion.replaceAll("[^\\d]", ""));
        int currentVersion = Integer.parseInt(getCurrentVersion().replaceAll("[^\\d]", ""));

        logger.info("Looking for updates...");

        if (newVersion > currentVersion) {
            logger.info("There is a new update available.");
            logger.info("Current version " + getCurrentVersion());
            logger.info("Newest version " + this.newVersion);
            logger.info("Download the new version here: https://www.spigotmc.org/resources/" + resourceId);

            if (plugin.getConfig().getBoolean("Auto-Download")) autoDownload.autoDownload(resourceId);
        } else {
            logger.info("Plugin is up-to-date");
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
            for (String message : messages) utility.message(sender, message);
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

            for (String string : message) utility.message(player, string);
        }
    }

    private void fetchUpdates() {
        try {
            URL url = new URL("http://yfermin.bplaced.net/spigot/plugins.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            JsonElement element = new JsonParser().parse(reader).getAsJsonArray().get(0);

            String newVersion = element.getAsJsonObject().get("PSettings").getAsJsonObject().get("version").getAsString();
            int resourceId = element.getAsJsonObject().get("PSettings").getAsJsonObject().get("resource-id").getAsInt();
            boolean checkForUpdates = element.getAsJsonObject().get("PSettings").getAsJsonObject().get("check-for-updates").getAsBoolean();

            this.newVersion = newVersion;
            this.resourceId = resourceId;
            this.checkForUpdates = checkForUpdates;

        } catch (IOException e) {
            logger.log(Level.WARNING, "Error while checking for updates.");
            logger.info(e.getMessage());
        }
    }

    private String getCurrentVersion() {
        return plugin.getDescription().getVersion();
    }
}