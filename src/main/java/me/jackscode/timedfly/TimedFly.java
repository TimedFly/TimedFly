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
        modules.forEach(Module::onModuleEnable);
    }

    @Override
    public void onDisable() {
        modules.forEach(Module::onModuleDisable);
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

        modules = moduleHandler.loadModules(dataFolder.toPath());

        if (modules == null) {
            System.out.println("Could not load any modules... Something happened.");
        } else if (modules.isEmpty()) {
            System.out.println("There were no modules to load");
        }
    }
}
