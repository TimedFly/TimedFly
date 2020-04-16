package me.jackscode.timedfly.api;

import me.jackscode.timedfly.enums.CommandType;
import me.jackscode.timedfly.exceptions.CommandException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    String name;
    String description;
    List<String> aliases;
    CommandType commandType;

    public Command(
            @NotNull String name,
            @NotNull CommandType commandType,
            @Nullable String description,
            @Nullable List<String> aliases
    ) throws CommandException {

        this.name = name;
        this.commandType = commandType;

        this.description = description;
        this.aliases = aliases == null ? new ArrayList<>() : aliases;
    }

    /**
     * Run the logic of this command.
     * every module must override this method if using a command.
     *
     * @param sender The command executor
     * @param args   Arguments of this command
     */
    public abstract void execute(CommandSender sender, String[] args);

    /**
     * Get the command's name. This must be present, else
     * a {@link CommandException} will be thrown.
     *
     * @return Command's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the description of this command.
     * If no description provide (if null),
     * and empty string will be retuned.
     *
     * @return Command's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the aliases of this command if any.
     * Returns and empty list if the aliases is null.
     *
     * @return Command's aliases
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Get the TYPE of the command from {@link CommandType} enum
     *
     * @return Command's typ
     */
    public CommandType getCommandType() {
        return commandType;
    }
}
