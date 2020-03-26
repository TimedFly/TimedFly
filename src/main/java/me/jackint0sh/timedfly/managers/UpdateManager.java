package me.jackint0sh.timedfly.managers;

import me.jackint0sh.timedfly.utilities.Config;
import me.jackint0sh.timedfly.utilities.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class UpdateManager {

    public int id;
    public String currentVersion;
    public boolean newUpdate;

    public UpdateManager(int id, String currentVersion) {
        this.id = id;
        this.currentVersion = currentVersion;
        this.newUpdate = false;
    }

    public void checkForUpdate(@Nullable Player player) throws IOException {
        boolean canCheck = Config.getConfig("config").get().getBoolean("Check-For-Updates.Enable");
        if (!canCheck) return;

        Set<CommandSender> recipients = new HashSet<>();
        ConsoleCommandSender console = Bukkit.getConsoleSender();

        if (player != null) recipients.add(player);
        recipients.add(console);
        recipients.forEach(sender -> MessageUtil.sendTranslation(sender, "update.checking_for_update"));

        String spigotVersion = getSpigotVersion();
        boolean isUpdateAvailable = compareVersions(spigotVersion);

        if (isUpdateAvailable) {
            recipients.forEach(sender ->
                    MessageUtil.sendTranslation(sender, "update.new_update", new String[][]{
                            new String[]{"[version]", spigotVersion},
                            new String[]{"[url]", "https://www.spigotmc.org/resources/" + id}
                    })
            );
        } else {
            recipients.forEach(sender -> MessageUtil.sendTranslation(sender, "update.no_new_update"));
        }
    }

    private boolean compareVersions(String version) {

        String pattern = "\\D+";

        int intVersion = Integer.parseInt(version.replaceAll(pattern, ""));
        int intCurrentVersion = Integer.parseInt(currentVersion.replaceAll(pattern, ""));

        return intVersion > intCurrentVersion;
    }

    private String getSpigotVersion() throws IOException {
        URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        InputStream inputStream = con.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);

        String inputLine;
        BufferedReader in = new BufferedReader(reader);
        StringBuilder content = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();

        return content.toString();
    }
}
