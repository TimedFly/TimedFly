package me.jackscode.timedfly.module;

public abstract class Module {

    /**
     * Get the name of the current module.
     * Every implementation must have this method.
     *
     * @return Name of the Module
     */
    public abstract String getName();

    /**
     * Get the description of the current module.
     * Every implementation must have this method.
     *
     * @return Description of the Module
     */
    public abstract String getDescription();

}
