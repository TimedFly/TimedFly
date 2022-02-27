package me.jackscode.timedfly.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.api.Messenger;
import me.jackscode.timedfly.api.Module;
import me.jackscode.timedfly.enums.CommandType;
import me.jackscode.timedfly.handlers.ModuleHandler;

public class Modules extends Command {
    private ModuleHandler moduleHandler;

    public Modules(ModuleHandler moduleHandler) {
        super("modules", CommandType.TIMED_FLY, "Enable/Disable modules", Arrays.asList("mod", "m"));
        this.moduleHandler = moduleHandler;
    }

    @Override
    public void execute(Messenger messenger, String[] args) {
        if (args.length < 2) {
            messenger.sendMessage("No args...");
            return;
        }

        File moduleFile = moduleHandler.getModulesPath().get(args[1]);
        if (moduleFile == null) {
            messenger.sendMessage("No module with that name was found");
            return;
        }

        switch (args[0]) {
            case "enable":
            case "on":
                Module enabledModule = moduleHandler.enableModule(moduleFile);
                if (enabledModule == null) {
                    messenger.sendMessage("Could not enable module: " + args[1]);
                }
                messenger.sendMessage("Enabled module: " + args[1]);
                break;
            case "disable":
            case "off":
                Optional<Module> optionalModule = moduleHandler.getModule(args[1]);
                if (!optionalModule.isPresent() || optionalModule.get() == null) {
                    messenger.sendMessage("No module with that name was found");
                    return;
                }
                moduleHandler.disableModule(optionalModule.get());
                messenger.sendMessage("Disabled module: " + args[1]);
                break;
            default:
                messenger.sendMessage("wrong usage: enable,disable");
                break;
        }
    }
}
