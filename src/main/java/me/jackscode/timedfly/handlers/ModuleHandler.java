package me.jackscode.timedfly.handlers;

import me.jackscode.timedfly.api.TimedFlyModule;
import me.jackscode.timedfly.api.TimedFlyModuleDescription;
import me.jackscode.timedfly.exceptions.ModuleException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModuleHandler {


    public List<TimedFlyModule> loadModules(Path path) {
        try {
            return Files.list(path)
                    .map(Path::toFile)
                    .map(this::enableModules)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public TimedFlyModule enableModules(File fileModule) {
        URLClassLoader classLoader = null;
        try {
            System.out.println("Attempting to load module: " + fileModule.getName());

            // Prepare to load class
            classLoader = new URLClassLoader(
                    new URL[]{fileModule.toURI().toURL()},
                    this.getClass().getClassLoader()
            );

            // Get module.yml file
            InputStream inputStream = classLoader.getResourceAsStream("module.yml");

            // module.yml must exist
            if (inputStream == null) {
                throw new ModuleException("There is no module.yml file on the module " + fileModule.getName());
            }

            // Read contents of module.yml file
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);

            // Load yml into a YamlConfiguration
            YamlConfiguration moduleConfig = new YamlConfiguration();
            moduleConfig.load(reader);

            // Create an instance of the module description and add the values
            TimedFlyModuleDescription moduleDescription = populateModuleDescription(moduleConfig, fileModule.getName());

            // Path to main class
            String main = moduleDescription.getMain();

            // Get class to load
            Class<?> clazz = Class.forName(main, true, classLoader);

            // Load class constructor
            Constructor<?> constructor = clazz.getConstructor();

            // Make new instance of the module
            Object instance = constructor.newInstance();

            // Class must extend Module abstract class
            if (!(instance instanceof TimedFlyModule)) {
                throw new ModuleException(main + " must implement Module");
            }

            TimedFlyModule module = (TimedFlyModule) instance;

            // Get the module's description field to be populated
            Field field = module.getClass().getSuperclass().getDeclaredField("moduleDescription");

            // Set private variable accessible to be able to change it
            field.setAccessible(true);

            // Populate module's description field
            field.set(module, moduleDescription);

            // Set field to private again (I don't know if it's necessary)
            field.setAccessible(false);
            System.out.println("Module " + fileModule.getName() + " has been loaded");
            return module;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (classLoader != null) {
                    classLoader.close();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return null;
        }
    }

    private TimedFlyModuleDescription populateModuleDescription(FileConfiguration moduleConfig, String module) throws ModuleException {
        // Get all the values from modules.yml file
        String main = moduleConfig.getString("main");
        String name = moduleConfig.getString("name");
        String description = moduleConfig.getString("description");
        String version = moduleConfig.getString("version");
        List<String> authors = moduleConfig.getStringList("authors");

        String moduleException = "There is no '%s' section on modules.yml of " + module;

        // Check to see if main, name, version is in the modules.yml file
        if (main == null) {
            throw new ModuleException(String.format(moduleException, "main"));
        } else if (name == null) {
            throw new ModuleException(String.format(moduleException, "name"));
        } else if (version == null) {
            throw new ModuleException(String.format(moduleException, "version"));
        }

        if (description == null) {
            description = "No description provided";
        }

        return new TimedFlyModuleDescription(main, name, description, version, authors);
    }

}
