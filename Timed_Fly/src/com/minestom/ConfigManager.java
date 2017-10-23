package com.minestom;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigManager {

	public void setupConfig(Plugin plugin) {
		FileConfiguration config = plugin.getConfig();
		if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
			plugin.saveDefaultConfig();
			config.createSection("Messages.SetItem");
			config.createSection("Messages.SetTime");
			config.createSection("Messages.SetPrice");
			config.createSection("Messages.Reload");
			config.createSection("Fly");
			config.set("Messages.SetItem.Usage", "&cTimedFly >> &eUsage: &7/tf setItem <itemID>");
			config.set("Messages.SetItem.Found", "&cTimedFly >> &eYou have changed the item of &7%itemid%.");
			config.set("Messages.SetItem.NotFound", "&cTimedFly >> &eI could not find the itemID &7%itemid%.");
			config.set("Messages.SetTime.Usage", "&cTimedFly >> &eUsage: &7/tf setTime <itemID> <minutes>");
			config.set("Messages.SetTime.Found", "&cTimedFly >> &eYou have set the time of &7%itemid% &eto &7%time%.");
			config.set("Messages.SetTime.NotFound", "&cTimedFly >> &eI could not find the itemID &7%itemid%.");
		}
	}

}
