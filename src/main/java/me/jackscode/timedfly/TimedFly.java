package me.jackscode.timedfly;

import lombok.SneakyThrows;
import me.jackscode.timedfly.api.Module;
import me.jackscode.timedfly.commands.TF;
import me.jackscode.timedfly.commands.TFly;
import me.jackscode.timedfly.handlers.CommandHandler;
import me.jackscode.timedfly.handlers.CurrencyHandler;
import me.jackscode.timedfly.handlers.ModuleHandler;
import me.jackscode.timedfly.managers.TimerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public final class TimedFly extends JavaPlugin {

    private CommandHandler commandHandler;
    private CurrencyHandler currencyHandler;
    private ModuleHandler moduleHandler;

    @Override
    public void onEnable() {
        this.createInstances();
        this.handleModules();
        this.enableCommands();
    }

    @SneakyThrows @Override
    public void onDisable() {
        this.moduleHandler.disableAllModules();
        TimerManager.stop();
    }

    private void createInstances() {
        this.commandHandler = new CommandHandler();
        this.currencyHandler = new CurrencyHandler();
        this.moduleHandler = new ModuleHandler(
                this.commandHandler,
                this.currencyHandler,
                this
        );
    }

    private void handleModules() {
        File dataFolder = new File(this.getDataFolder(), "/modules/");

        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) {
                System.out.println("Could not make modules folder");
                return;
            }
        }

        this.moduleHandler.enableModules(dataFolder.toPath());

        List<Module> modules = this.moduleHandler.getModules();

        if (modules == null) {
            System.out.println("Could not load any modules... Something happened.");
        } else if (modules.isEmpty()) {
            System.out.println("There were no modules to load");
        }
    }

    private void enableCommands() {
        this.getCommand("timedfly").setExecutor(new TF(this.commandHandler));
        this.getCommand("tfly").setExecutor(new TFly(this.commandHandler));
    }
}
