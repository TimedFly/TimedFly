package me.jackscode.timedfly.handlers;

import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.api.Module;
import me.jackscode.timedfly.exceptions.CommandException;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CommandHandler {

    private final Set<Command> commands;

    public CommandHandler() {
        this.commands = new HashSet<>();
    }

    public void register(@NotNull Command command, @NotNull Object module) throws CommandException {
        if (this.commands.stream().anyMatch(cmd -> cmd.getName().equals(command.getName()))) {
            throw new CommandException(String.format(
                    "Command with name %s already exists.",
                    command.getName()
            ));
        }

        this.commands.add(command);
        if (module instanceof Module) ((Module) module).getCommandList().add(command);

        Bukkit.getLogger().info("Command registered: " + command.getName());
    }

    public void unregister(@NotNull Command command) throws CommandException {
        if (this.commands.stream().noneMatch(cmd -> cmd.getName().equals(command.getName()))) {
            throw new CommandException(String.format(
                    "Command with name %s does not exists.",
                    command.getName()
            ));
        }

        this.commands.remove(command);

        Bukkit.getLogger().info("Command unregistered: " + command.getName());
    }

    public void unregisterAll() {
        this.commands.clear();

        Bukkit.getLogger().info("All commands had been unregistered");
    }

    public Set<Command> getCommands() {
        return commands;
    }
}
