package me.jackscode.timedfly;

import me.jackscode.timedfly.module.Module;
import me.jackscode.timedfly.module.ModuleHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public final class TimedFly extends JavaPlugin {

    private List<Module> modules;

    @Override
    public void onEnable() {
        handleModules();
    }

    @Override
    public void onDisable() {

    }

    private void handleModules() {
        ModuleHandler moduleHandler = new ModuleHandler();
        File dataFolder = new File(this.getDataFolder(), "modules/");

        modules = moduleHandler.loadModules(dataFolder.toPath());

        if (modules == null) {
            System.out.println("Could not load any modules... Something happened.");
        } else if (modules.isEmpty()) {
            System.out.println("There were no modules to load");
        }
    }
}
