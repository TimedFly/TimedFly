package me.jackscode.timedfly.api;

import me.jackscode.timedfly.TimedFly;
import me.jackscode.timedfly.exceptions.CommandException;
import me.jackscode.timedfly.handlers.CommandHandler;
import me.jackscode.timedfly.handlers.CurrencyHandler;
import me.jackscode.timedfly.handlers.ModuleHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    private final List<Command> commandList;

    private ModuleDescription moduleDescription;
    private CommandHandler commandHandler;
    private CurrencyHandler currencyHandler;
    private ModuleHandler moduleHandler;
    private TimedFly plugin;


    {
        this.commandList = new ArrayList<>();
    }

    /**
     * Get the description of the module in different methods.
     *
     * @return The description of the module
     */
    public ModuleDescription getModuleDescription() {
        return moduleDescription;
    }

    /**
     * Get all commands available on this module
     *
     * @return List of commands from this module
     */
    public List<Command> getCommandList() {
        return commandList;
    }

    /**
     * Get the class in charge of registering and unregistering
     * commands from modules. Useful for unregistering commands from
     * other modules.
     *
     * @return Command handler of the main plugin
     */
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    /**
     * Get the class in charge of managing currencies.
     * Useful for adding, removing, and getting the enabled currencies.
     *
     * @return Currency handler of the main plugin
     */
    public CurrencyHandler getCurrencyHandler() {
        return currencyHandler;
    }

    /**
     * Get the class in charge of enabling and disabling modules.
     * Can be used to enable or disable other modules at runtime.
     *
     * @return Module handler of the main plugin
     */
    public ModuleHandler getModuleHandler() {
        return moduleHandler;
    }

    /**
     * Get an instance of the main plugin, makes it so you can get
     * the config, the data folder, etc...
     *
     * @return Main plugin instance
     */
    public TimedFly getPlugin() {
        return plugin;
    }

    /**
     * Gets called when the module gets enabled.
     * Every implementation must have this method.
     */
    public abstract void onModuleEnable();

    /**
     * Gets called when the module gets disabled.
     * Every implementation must have this method.
     */
    public abstract void onModuleDisable();

    /**
     * Registers commands on the main plugin.
     * Commands are not actually the text after the command prefix '/'.
     * It is the arguments after the real command.
     * Example: /some command, where command will be the registered command.
     *
     * @param commands Commands to be registered
     * @throws CommandException If name already exists this will be thrown
     */
    public void registerCommands(@NotNull Command... commands) throws CommandException {
        for (Command command : commands) {
            commandHandler.register(command);
            commandList.add(command);
        }
    }


}
