package me.jackscode.timedfly.commands;

import me.jackscode.timedfly.api.Module;
import me.jackscode.timedfly.handlers.ModuleHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ToggleModule implements CommandExecutor {

    private final List<Module> modules;
    private final ModuleHandler moduleHandler;
    private final Plugin plugin;

    public ToggleModule(ModuleHandler moduleHandler, Plugin plugin) {
        this.moduleHandler = moduleHandler;
        this.modules = moduleHandler.getModules();
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args[0].equals("list")) {
            sender.sendMessage(Arrays.toString(modules.stream().map(module -> module.getModuleDescription().getName()).toArray()));
            return true;
        }

        if (args.length < 2) return false;

        String moduleName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (args[0].equals("enable")) {
            File module = new File(plugin.getDataFolder(), "/modules/" + moduleName + ".jar");
            moduleHandler.enableModule(module);
        } else if (args[0].equals("disable")) {
            Optional<Module> moduleOptional = modules.stream()
                    .filter(m -> m.getModuleDescription().getName().equals(moduleName))
                    .findFirst();

            if (!moduleOptional.isPresent()) return false;

            Module module = moduleOptional.get();
            moduleHandler.disableModule(module);
        }
        return true;
    }
}
