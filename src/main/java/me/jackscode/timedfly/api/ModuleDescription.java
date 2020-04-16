package me.jackscode.timedfly.api;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModuleDescription {

    private final String main;
    private final String name;
    private final String description;
    private final String version;
    private final List<String> authors;

    public ModuleDescription(
            @NotNull String main,
            @NotNull String name,
            @NotNull String description,
            @NotNull String version,
            @NotNull List<String> authors
    ) {
        this.main = main;
        this.name = name;
        this.description = description;
        this.version = version;
        this.authors = authors;
    }

    /**
     * Get the path of to the main class of this module.
     *
     * @return Path of the main class
     */
    public String getMain() {
        return this.main;
    }

    /**
     * Get the name of the current module.
     *
     * @return Name of the Module
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the description of the current module.
     *
     * @return Description of the Module
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Get the version of the current module.
     *
     * @return Version of Module
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Get the version of the current module.
     *
     * @return Version of Module
     */
    public List<String> getAuthors() {
        return this.authors;
    }
}
