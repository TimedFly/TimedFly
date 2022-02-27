package me.jackscode.timedfly;

import lombok.SneakyThrows;
import me.jackscode.timedfly.api.Module;
import me.jackscode.timedfly.api.Permission;
import me.jackscode.timedfly.commands.Main;
import me.jackscode.timedfly.commands.TFly;
import me.jackscode.timedfly.commands.TimerCommand;
import me.jackscode.timedfly.exceptions.CommandException;
import me.jackscode.timedfly.handlers.CommandHandler;
import me.jackscode.timedfly.handlers.CurrencyHandler;
import me.jackscode.timedfly.handlers.ModuleHandler;
import me.jackscode.timedfly.listeners.FallDamage;
import me.jackscode.timedfly.managers.TimerManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public final class TimedFly extends JavaPlugin {

    private CurrencyHandler currencyHandler;
    private CommandHandler commandHandler;
    private ModuleHandler moduleHandler;

    @SneakyThrows
    @Override
    public void onEnable() {
        this.createInstances();
        this.handleModules();
        this.saveDefaultPermissions();
        this.enableCommands();
        this.registerEvents(new FallDamage());

        TimerManager.start();
    }

    @Override
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
                this);
    }

    private void handleModules() {
        File dataFolder = new File(this.getDataFolder(), "/modules/");

        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            System.out.println("Could not make modules folder");
            return;
        }

        this.moduleHandler.enableModules(dataFolder.toPath());

        List<Module> modules = this.moduleHandler.getModules();

        if (modules == null) {
            System.out.println("Could not load any modules... Something happened.");
        } else if (modules.isEmpty()) {
            System.out.println("There were no modules to load");
        }
    }

    private void enableCommands() throws CommandException {
        this.getCommand("timedfly").setExecutor(new Main(this.commandHandler));
        this.getCommand("tfly").setExecutor(new TFly(this.commandHandler));

        this.commandHandler.register(new TimerCommand());
    }

    private void saveDefaultPermissions() {
        Permission.add("fly.add", "Add fly to self or others");
        Permission.add("fly.set", "Set fly to self or others");
    }

    public void registerEvents(Listener... listener) {
        Arrays.stream(listener)
                .forEachOrdered(event -> {
                    this.getServer()
                            .getPluginManager()
                            .registerEvents(event, this);
                    System.out.println("Event " + event.getClass().getSimpleName() + " registered");
                });
    }
}
