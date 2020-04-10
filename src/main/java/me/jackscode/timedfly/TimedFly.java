package me.jackscode.timedfly;

import me.jackscode.timedfly.module.Module;
import me.jackscode.timedfly.module.ModuleHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.stream.Stream;

public final class TimedFly extends JavaPlugin {

    private Stream<Module> modules;

    @Override
    public void onEnable() {
        handleModules();
        modules.forEach(Module::onModuleEnable);
    }

    @Override
    public void onDisable() {
        modules.forEach(Module::onModuleDisable);
    }

    private void handleModules() {
        ModuleHandler moduleHandler = new ModuleHandler();
        File dataFolder = new File(this.getDataFolder(), "modules/");

        modules = moduleHandler.loadModules(dataFolder.toPath());

        if (modules == null) {
            System.out.println("Could not load any modules... Something happened.");
        } else if (modules.count() == 0) {
            System.out.println("There were no modules to load");
        }
    }
}
