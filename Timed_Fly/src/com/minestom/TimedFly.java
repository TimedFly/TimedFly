package com.minestom;

import java.io.File;
import java.util.Map.Entry;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.minestom.CMDs.FlyCMD;
import com.minestom.CMDs.MainCMD;
import com.minestom.Languages.DataConfig;
import com.minestom.Languages.ItemsConfig;
import com.minestom.Languages.LangConfig;
import com.minestom.Updater.Updater;
import com.minestom.Utilities.GUI.GUIListener;
import com.minestom.Utilities.Others.GeneralListener;

import net.milkbowl.vault.economy.Economy;

public class TimedFly extends JavaPlugin {
	public static Economy economy;
	public static TimedFly plugin;
	private DataConfig data = DataConfig.getInstance();
	private LangConfig lang = LangConfig.getInstance();
	private ItemsConfig items = ItemsConfig.getInstance();

	@Override
	public void onEnable() {
		plugin = this;
		data.setup(this);
		lang.createFiles(this);
		items.createFiles(this);
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
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") == true) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&cTimedFly >> &7PlaceholderAPI found, using it for item's lore and name."));
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
					"&cTimedFly >> &7PlaceholderAPI not found, you should install it"));
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
		if (plugin.getConfig().getBoolean("SaveTimeLeave") == false) {
			for (String str : data.getData().getKeys(false)) {
				UUID uuid = UUID.fromString(str);
				Integer p = data.getData().getInt(str + ".Time");
				GUIListener.cooldown.put(uuid, p);
				data.getData().set(str, null);
				data.saveData();
			}
		} else {
			for (Player player : Bukkit.getOnlinePlayers()) {
				int time = data.getData().getInt(player.getUniqueId() + ".Time");
				if (data.getData().contains(player.getUniqueId().toString())) {
					GUIListener.cooldown.put(player.getUniqueId(), time);
					data.getData().set(player.getUniqueId().toString(), null);
					data.saveData();
					if (player.getAllowFlight() == false || player.isFlying() == false) {
						player.setAllowFlight(true);
						player.setFlying(true);
					}
				}
			}
		}
		if (plugin.getConfig().getBoolean("Check-For-Updates") == true) {
			Updater.sendUpdater();
		}
		if (Bukkit.getVersion().contains("1.8")) {
			lang.getLang().set("Announcer.Sound", "ORB_PICKUP");
			lang.getLang().set("Fly.DisabledSound", "WITHER_DEATH");
			saveConfig();
		}
	}

	@Override
	public void onDisable() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			List<String> worlds = plugin.getConfig().getStringList("Enabled-Worlds");
			for (String world : worlds) {
				World w = Bukkit.getWorld(world);
				if (player.getWorld() == w) {
					GUIListener.godmode.put(player.getUniqueId(), 6);
					if (player.getAllowFlight() == true || player.isFlying() == true) {
						player.setAllowFlight(false);
						player.setFlying(false);
					}
				}
			}
		}
		if (plugin.getConfig().getBoolean("SaveTimeLeave") == false) {
			for (Entry<UUID, Integer> timePlayer : GUIListener.cooldown.entrySet()) {
				data.getData().set(timePlayer.getKey() + ".Time", timePlayer.getValue());
				data.saveData();
			}
		} else {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (GUIListener.cooldown.containsKey(player.getUniqueId())) {
					data.getData().set(player.getUniqueId() + ".Time", GUIListener.cooldown.get(player.getUniqueId()));
					data.saveData();
				}
			}
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
