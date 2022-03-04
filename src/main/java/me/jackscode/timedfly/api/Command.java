package me.jackscode.timedfly.api;

import me.jackscode.timedfly.enums.CommandType;
import me.jackscode.timedfly.exceptions.CommandException;
import net.md_5.bungee.api.chat.ClickEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Command {

    String name;
    String description;
    List<String> aliases;
    CommandType commandType;
    Map<String, List<Argument>> args;

    protected Command(
            @NotNull String name,
            @NotNull CommandType commandType,
            @Nullable String description,
            @Nullable List<String> aliases,
            @NotNull Map<String, List<Argument>> args) {

        this.name = name;
        this.commandType = commandType;
        this.args = args;
        this.description = description;
        this.aliases = aliases == null ? new ArrayList<>() : aliases;
    }

    protected Command(
            @NotNull String name,
            @NotNull CommandType commandType,
            @Nullable String description,
            @Nullable List<String> aliases) {

        this.name = name;
        this.commandType = commandType;
        this.description = description;
        this.args = new HashMap<>();
        this.aliases = aliases == null ? new ArrayList<>() : aliases;
    }

    protected Command(
            @NotNull String name,
            @NotNull CommandType commandType,
            @Nullable String description,
            @NotNull Map<String, List<Argument>> args) {
        this.name = name;
        this.commandType = commandType;

        this.description = description;
        this.args = args;
        this.aliases = new ArrayList<>();
    }

    protected Command(
            @NotNull String name,
            @NotNull CommandType commandType,
            @Nullable String description) {
        this.name = name;
        this.commandType = commandType;

        this.description = description;
        this.args = new HashMap<>();
        this.aliases = new ArrayList<>();
    }

    protected Command(
            @NotNull String name,
            @NotNull CommandType commandType) {
        this.name = name;
        this.commandType = commandType;

        this.description = null;
        this.args = new HashMap<>();
        this.aliases = new ArrayList<>();
    }

    /**
     * Run the logic of this command.
     * every module must override this method if using a command.
     *
     * @param messenger The command executor
     * @param args      Arguments of this command
     */
    public abstract void execute(Messenger messenger, String[] args);

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
     * Get the arguments of this command if any.
     * Ex. /timedfly timer add <time> [player]
     * <time> is a required argument.
     * [player] is an optional argumen.
     * Returns and empty list if the aliases is null.
     *
     * @return Command's arguments in a list of required and optional arguments
     */
    public Map<String, List<Argument>> getArgs() {
        return args;
    }

    /**
     * Add additional arguments to the module main command.
     * Ex. /timedfly timer add <time> [player]
     * <time> is a required argument.
     * [player] is an optional argumen.
     *
     * @param name      The name of the argument
     * @param arguments Additional arguments of this command
     * 
     * @return Command's arguments in a list of required and optional arguments
     */
    public void addArgs(String name, List<Argument> arguments) {
        this.args.put(name, arguments);
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

    public void sendHelpMessage(Messenger messenger, boolean header) {
        String type = this.getCommandType() == CommandType.TFLY ? "/tfly" : "/timedfly";
        String format = "&7%s %s help".formatted(type, this.getName());

        if (header)
            messenger.sendMessage("&c  TimedFly Help: " + this.getName(), "&6------------------");

        messenger.sendHoverableMessage(format,
                new Messenger.OnClick(ClickEvent.Action.RUN_COMMAND, format.replace("&7", "")),
                "Display the help message if this command");

        for (Entry<String, List<Argument>> entry : this.getArgs().entrySet()) {
            String key = entry.getKey();
            List<Argument> values = entry.getValue();

            String arguments = "";
            String requirements = "";

            if (values != null) {
                arguments = String.join(" ", values.stream().map(Argument::toString).toList());
                requirements = String.join("\n", values.stream()
                        .map(arg -> {
                            StringBuilder message = new StringBuilder();

                            message.append("Argument %s is %s.%n".formatted(arg, arg.getType()));
                            if (arg.getDesctiption() != null)
                                message.append(" Desc: " + arg.getDesctiption() + "\n");
                            if (arg.getExample() != null)
                                message.append(" Ex: " + arg.getExample());
                            message.append("\n");
                            return message.toString();
                        })
                        .toList());
            }

            format = "&7%s %s %s %s".formatted(type, this.getName(), key, arguments);
            messenger.sendHoverableMessage(format,
                    new Messenger.OnClick(ClickEvent.Action.SUGGEST_COMMAND, format.replace("&7", "")),
                    this.getDescription(),
                    requirements.trim());
        }
        messenger.sendMessage("");
    }

    public void sendHelpMessage(Messenger messenger) {
        this.sendHelpMessage(messenger, false);
    }

    public boolean invalidArgsLength(Messenger messenger, String[] args, int minSize) {
        if (args.length >= minSize)
            return false;
        messenger.sendHoverableMessage(
                "&cIncorrect usage. Try &6/timedfly %s help".formatted(this.name),
                new Messenger.OnClick(ClickEvent.Action.RUN_COMMAND, "/timedfly %s help".formatted(this.name)),
                "Click to display help.");
        return true;
    }
}
