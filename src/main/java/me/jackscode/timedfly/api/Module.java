package me.jackscode.timedfly.api;

import me.jackscode.timedfly.TimedFly;
import me.jackscode.timedfly.exceptions.CommandException;
import me.jackscode.timedfly.handlers.CommandHandler;
import me.jackscode.timedfly.handlers.ModuleHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    private ModuleDescription moduleDescription;
    private CommandHandler commandHandler;
    private ModuleHandler moduleHandler;
    private TimedFly plugin;
    private final List<Command> commandList;

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

    public List<Command> getCommandList() {
        return commandList;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    public ModuleHandler getModuleHandler() {
        return moduleHandler;
    }

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
    public void registerCommands(Command... commands) throws CommandException {
        for (Command command : commands) {
            commandHandler.register(command);
            commandList.add(command);
        }
    }


}
