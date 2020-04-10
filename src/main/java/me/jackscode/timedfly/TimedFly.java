package me.jackscode.timedfly;

import me.jackscode.timedfly.api.TimedFlyModule;
import me.jackscode.timedfly.handlers.ModuleHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public final class TimedFly extends JavaPlugin {

    private List<TimedFlyModule> timedFlyModules;

    @Override
    public void onEnable() {
        handleModules();
        timedFlyModules.forEach(TimedFlyModule::onModuleEnable);
    }

    @Override
    public void onDisable() {
        timedFlyModules.forEach(TimedFlyModule::onModuleDisable);
    }

    private void handleModules() {
        File dataFolder = new File(this.getDataFolder(), "/modules/");

        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) {
                System.out.println("Could not make modules folder");
                return;
            }
        }

        ModuleHandler moduleHandler = new ModuleHandler();

        timedFlyModules = moduleHandler.loadModules(dataFolder.toPath());

        if (timedFlyModules == null) {
            System.out.println("Could not load any modules... Something happened.");
        } else if (timedFlyModules.isEmpty()) {
            System.out.println("There were no modules to load");
        }
    }
}
