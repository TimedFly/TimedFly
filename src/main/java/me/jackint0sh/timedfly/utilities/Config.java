package me.jackint0sh.timedfly.utilities;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Config {

    private Plugin plugin;
    private String name;
    private File file;
    private FileConfiguration fileConfiguration;
    private static ConcurrentMap<String, Config> configMap = new ConcurrentHashMap<>();

    public Config(String name, Plugin plugin) {
        this.name = name + ".yml";
        this.plugin = plugin;
        Config.configMap.put(name, this);
    }

    public Config create() throws IOException, InvalidConfigurationException {
        this.file = new File(this.plugin.getDataFolder(), name);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }
        fileConfiguration = new YamlConfiguration();
        fileConfiguration.load(file);
        return this;
    }

    public FileConfiguration get() {
        return this.fileConfiguration;
    }

    public void save() throws IOException {
        fileConfiguration.save(file);
    }

    public void reload(boolean save) throws IOException {
        if (save) save();
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
        Config.configMap.put(name, this);
    }

    public String getName() {
        return name;
    }

    public static Config getConfig(String name) {
        return configMap.get(name);
    }

    public static Map<String, Config> getConfigs() {
        return configMap;
    }
}
