package me.jackscode.timedfly;

import lombok.SneakyThrows;
import me.jackscode.timedfly.api.Module;
import me.jackscode.timedfly.api.Permission;
import me.jackscode.timedfly.commands.Main;
import me.jackscode.timedfly.commands.TFly;
import me.jackscode.timedfly.commands.TabComplete;
import me.jackscode.timedfly.commands.TimerCommand;
import me.jackscode.timedfly.enums.CommandType;
import me.jackscode.timedfly.commands.Modules;
import me.jackscode.timedfly.exceptions.CommandException;
import me.jackscode.timedfly.handlers.CommandHandler;
import me.jackscode.timedfly.handlers.CurrencyHandler;
import me.jackscode.timedfly.handlers.ModuleHandler;
import me.jackscode.timedfly.listeners.FallDamage;
import me.jackscode.timedfly.managers.TimerManager;

import org.bukkit.Bukkit;
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
            Bukkit.getLogger().severe("Could not make modules folder");
            return;
        }

        this.copyDefaultModules();
        this.moduleHandler.enableModules(dataFolder.toPath());

        List<Module> modules = this.moduleHandler.getModules();

        if (modules == null) {
            Bukkit.getLogger().severe("Could not load any modules... Something happened.");
        } else if (modules.isEmpty()) {
            Bukkit.getLogger().warning("There were no modules to load");
        }
    }

    private void copyDefaultModules() {
        String[] defaultModules = {  };

        Arrays.stream(defaultModules).forEach(moduleName -> {
            this.saveResource("modules/" + moduleName + ".jar", true);
        });
    }

    private void enableCommands() throws CommandException {
        this.getCommand("timedfly").setExecutor(new Main(this.commandHandler));
        this.getCommand("tfly").setExecutor(new TFly(this.commandHandler));

        this.commandHandler.register(new TimerCommand(), this);
        this.commandHandler.register(new Modules(moduleHandler), this);

        Bukkit.getPluginCommand("timedfly").setTabCompleter(new TabComplete(commandHandler, CommandType.TIMED_FLY));
        Bukkit.getPluginCommand("tfly").setTabCompleter(new TabComplete(commandHandler, CommandType.TFLY));
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
                    Bukkit.getLogger().info("Event " + event.getClass().getSimpleName() + " registered");
                });
    }
}
