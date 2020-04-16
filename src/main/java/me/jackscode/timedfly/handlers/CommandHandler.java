package me.jackscode.timedfly.handlers;

import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.exceptions.CommandException;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class CommandHandler {

    private final Set<Command> commands;

    public CommandHandler() {
        this.commands = new HashSet<>();
    }

    public void register(@NotNull Command command) throws CommandException {
        if (commands.stream().anyMatch(cmd -> cmd.getName().equals(command.getName()))) {
            throw new CommandException(String.format(
                    "Command with name %s already exists.",
                    command.getName()
            ));
        }

        commands.add(command);

        System.out.println("Command registered: " + command.getName());
    }

    public void unregister(@NotNull Command command) throws CommandException {
        if (commands.stream().noneMatch(cmd -> cmd.getName().equals(command.getName()))) {
            throw new CommandException(String.format(
                    "Command with name %s does not exists.",
                    command.getName()
            ));
        }

        commands.remove(command);

        System.out.println("Command unregistered: " + command.getName());
    }

    public void unregisterAll() {
        commands.clear();

        System.out.println("All commands had been unregistered");
    }

    public Set<Command> getCommands() {
        return commands;
    }
}
