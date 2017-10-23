package com.minestom;

import java.io.File;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.minestom.CMDs.FlyCMD;
import com.minestom.CMDs.MainCMD;
import com.minestom.Configurations.DataConfig;
import com.minestom.Updater.Updater;
import com.minestom.Utilities.GUIListener;
import com.minestom.Utilities.GeneralListener;

import net.milkbowl.vault.economy.Economy;

public class TimedFly extends JavaPlugin {
	public static Economy economy;
	public static TimedFly plugin;
	private DataConfig data = DataConfig.getInstance();

	@Override
	public void onEnable() {
		plugin = this;
		data.setup(this);
		if (!setupEconomy()) {
			Bukkit.getLogger().severe(String.format("¡ìcTimedFly >> Disabled due to no Vault dependency found!",
					getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		} else {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cTimedFly >> &7Hooked to Vault"));
		}
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("ActionBarAPI") == true) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&cTimedFly >> &7ActionBarAPI found, using it for ActionBar messages."));
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&cTimedFly >> &7ActionBarAPI not found, disabling ActionBar messages."));
		}
		if (!new File(this.getDataFolder(), "config.yml").exists()) {
			this.saveDefaultConfig();
		}
		getCommand("timedfly").setExecutor(new MainCMD());
		getCommand("fly").setExecutor(new FlyCMD());
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
				"&cTimedFly >> &7The plugin has been enabled and its ready to use."));
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(new GUIListener(), this);
		pm.registerEvents(new GeneralListener(), this);
		for (String str : data.getData().getKeys(false)) {
			UUID uuid = UUID.fromString(str);
			Integer p = data.getData().getInt(str + ".Time");
			GUIListener.cooldown.put(uuid, p);
			data.getData().set(str, null);
			data.saveData();
		}
		Updater.sendUpdater();
		if (Bukkit.getVersion().contains("1.8")) {
			getConfig().set("Announce.Sound", "ORB_PICKUP");
			getConfig().set("Fky.DisabledSound", "WITHER_DEATH");
			saveConfig();
		}
	}

	@Override
	public void onDisable() {
		for (Entry<UUID, Integer> timePlayer : GUIListener.cooldown.entrySet()) {
			data.getData().set(timePlayer.getKey() + ".Time", timePlayer.getValue());
			data.saveData();
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getOpenInventory().getTitle().equals(
					ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Gui.DisplayName")))) {
				player.closeInventory();
			}
		}
		Bukkit.getConsoleSender().sendMessage(
				ChatColor.translateAlternateColorCodes('&', "&cTimedFly >> &7The plugin has been disabled"));
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	public static TimedFly getInstance() {
		return plugin;
	}

}
