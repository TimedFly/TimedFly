package me.jackscode.timedfly.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import me.jackscode.timedfly.api.Argument;
import me.jackscode.timedfly.api.ArgumentType;
import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.api.Messenger;
import me.jackscode.timedfly.api.Module;
import me.jackscode.timedfly.enums.CommandType;
import me.jackscode.timedfly.handlers.ModuleHandler;
import net.md_5.bungee.api.chat.ClickEvent;

public class Modules extends Command {
    private ModuleHandler moduleHandler;

    public Modules(ModuleHandler moduleHandler) {
        super(
                "modules",
                CommandType.TIMED_FLY,
                "Enable/Disable modules",
                Arrays.asList("mod", "m"));

        this.moduleHandler = moduleHandler;

        this.addArgs("enable", Arrays.asList(
                new Argument("name",
                        ArgumentType.REQUIRED,
                        "Name of the module to enable.",
                        "TimerMessages")));
        this.addArgs("disable", Arrays.asList(
                new Argument("name",
                        ArgumentType.REQUIRED,
                        "Name of the module to disable.",
                        "Databases",
                        moduleHandler.getModules()
                                .stream()
                                .map(module -> module.getModuleDescription().getName())
                                .toList())));
    }

    @Override
    public void execute(Messenger messenger, String[] args) {
        if (this.invalidArgsLength(messenger, args, 1))
            return;

        switch (args[0]) {
            case "help":
            case "h":
                this.sendHelpMessage(messenger, true);
                break;
            case "enable":
            case "on":
                File moduleFile = moduleHandler.getModulesPath().get(args[1]);
                if (moduleFile == null) {
                    messenger.sendMessage("No module with that name was found");
                    return;
                }

                Module enabledModule = moduleHandler.enableModule(moduleFile);
                if (enabledModule == null) {
                    messenger.sendMessage("&cCould not enable module: " + args[1]);
                }
                messenger.sendMessage("&aEnabled module: &7" + args[1]);
                break;
            case "disable":
            case "off":
                Optional<Module> optionalModule = moduleHandler.getModule(args[1]);
                if (!optionalModule.isPresent() || optionalModule.get() == null) {
                    messenger.sendMessage("&cNo module with that name was found");
                    return;
                }
                moduleHandler.disableModule(optionalModule.get());
                messenger.sendMessage("&aDisabled module: &7" + args[1]);
                break;
            default:
                messenger.sendHoverableMessage(
                        String.format("&cCommand with name &3%s &cwas not found. Try &6/timedfly modules help",
                                args[0]),
                        new Messenger.OnClick(ClickEvent.Action.RUN_COMMAND, "/timedfly modules help"),
                        "Click to display help.");
                break;
        }
    }
}
