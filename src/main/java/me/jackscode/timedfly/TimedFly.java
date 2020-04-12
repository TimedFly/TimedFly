package me.jackscode.timedfly;

import me.jackscode.timedfly.api.Module;
import me.jackscode.timedfly.commands.ToggleModule;
import me.jackscode.timedfly.handlers.ModuleHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public final class TimedFly extends JavaPlugin {

    private ModuleHandler moduleHandler;

    @Override
    public void onEnable() {
        handleModules();
        this.getCommand("timedfly").setExecutor(new ToggleModule(moduleHandler, this));
    }

    @Override
    public void onDisable() {
        List<Module> modules = moduleHandler.getModules();
        modules.forEach(module -> moduleHandler.disableModule(module, false));
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
        moduleHandler.enableModules(dataFolder.toPath());

        this.moduleHandler = moduleHandler;

        List<Module> modules = moduleHandler.getModules();

        if (modules == null) {
            System.out.println("Could not load any modules... Something happened.");
        } else if (modules.isEmpty()) {
            System.out.println("There were no modules to load");
        }
    }
}
