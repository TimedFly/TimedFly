package me.jackscode.timedfly.api;

import me.jackscode.timedfly.enums.CommandType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    String name;
    String description;
    List<String> aliases;
    CommandType commandType;

    public Command(String name, String description, @Nullable List<String> aliases, CommandType commandType) {
        this.name = name;
        this.description = description;
        this.aliases = aliases == null ? new ArrayList<>() : aliases;
        this.commandType = commandType;
    }

    public abstract void execute(CommandSender sender, String[] args);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public CommandType getCommandType() {
        return commandType;
    }
}
