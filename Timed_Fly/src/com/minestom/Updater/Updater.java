package com.minestom.Updater;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.entity.Player;

import com.minestom.TimedFly;

public class Updater {
	private static TimedFly update = TimedFly.getInstance();

	public static void sendUpdater(Player player) {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php")
					.openConnection();
			con.setDoOutput(true);
			con.setConnectTimeout(2000);
			con.setRequestMethod("POST");
			con.getOutputStream()
					.write("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=48668"
							.getBytes("UTF-8"));

			String spigotVersionString = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine()
					.replaceAll("[a-zA-Z ]", "");

			int spigotVersion = Integer.valueOf(spigotVersionString.replace(".", "")).intValue();
			int actualVersion = Integer.valueOf(update.getDescription().getVersion().replace(".", "")).intValue();

			if (spigotVersion > actualVersion) {
				player.sendMessage("");
				player.sendMessage("¡ì7--------¡ìcTimedFly¡ì7--------");
				player.sendMessage("");
				player.sendMessage("¡ìeNew version available: ¡ìa" + spigotVersionString);
				player.sendMessage("¡ìeYou have: ¡ìa" + update.getDescription().getVersion());
				player.sendMessage("¡ìeDownload:¡ìa https://www.spigotmc.org/resources/timed-fly-1-8-1-12-2.48668/");
				player.sendMessage("");
			}
		} catch (Exception ex) {
			player.sendMessage("¡ìcTimedFly >> Failed to check for an update on spigot.");
		}
	}

	public static void sendUpdater() {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php")
					.openConnection();
			con.setDoOutput(true);
			con.setConnectTimeout(2000);
			con.setRequestMethod("POST");
			con.getOutputStream()
					.write("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=48668"
							.getBytes("UTF-8"));

			String spigotVersionString = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine()
					.replaceAll("[a-zA-Z ]", "");

			int spigotVersion = Integer.valueOf(spigotVersionString.replace(".", "")).intValue();
			int actualVersion = Integer.valueOf(update.getDescription().getVersion().replace(".", "")).intValue();

			if (spigotVersion > actualVersion) {
				update.getServer().getConsoleSender()
						.sendMessage("¡ìcNew version of TimedFly available ¡ìb" + spigotVersionString);
				update.getServer().getConsoleSender()
						.sendMessage("¡ìcYou have ¡ìb" + update.getDescription().getVersion());
				update.getServer().getConsoleSender()
						.sendMessage("¡ìcDownload: https://www.spigotmc.org/resources/timed-fly-1-8-1-12-2.48668/");
			} else {
				update.getServer().getConsoleSender()
						.sendMessage("¡ìcTimedFly >> ¡ì7Plugin is up to date, No updates available at this time.");
			}
		} catch (Exception ex) {
			update.getServer().getConsoleSender().sendMessage("¡ìcTimedFly >> Failed to check for an update on spigot.");
		}
	}

}