import me.jackscode.timedfly.api.Command;
import me.jackscode.timedfly.api.Module;
import me.jackscode.timedfly.enums.CommandType;
import me.jackscode.timedfly.exceptions.CommandException;
import me.jackscode.timedfly.handlers.ModuleHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ToggleModule extends Command {

    private final List<Module> modules;
    private final ModuleHandler moduleHandler;
    private final Plugin plugin;

    public ToggleModule(ModuleHandler moduleHandler, Plugin plugin) throws CommandException {
        super(
                "toggle",
                CommandType.TIMED_FLY,
                "Toggles modules on and off",
                null
        );
        this.moduleHandler = moduleHandler;
        this.modules = moduleHandler.getModules();
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) return;

        if (args[0].equals("list")) {
            sender.sendMessage(Arrays.toString(modules.stream().map(module -> module.getModuleDescription().getName()).toArray()));
            return;
        }

        if (args.length == 1) return;

        String moduleName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        if (args[0].equals("enable")) {
            File module = new File(plugin.getDataFolder(), "/modules/" + moduleName + ".jar");
            moduleHandler.enableModule(module);
        } else if (args[0].equals("disable")) {
            Optional<Module> moduleOptional = modules.stream()
                    .filter(m -> m.getModuleDescription().getName().equals(moduleName))
                    .findFirst();

            if (!moduleOptional.isPresent()) return;

            Module module = moduleOptional.get();
            moduleHandler.disableModule(module);
        }
    }
}
