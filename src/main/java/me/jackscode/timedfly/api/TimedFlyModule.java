package me.jackscode.timedfly.api;

public abstract class TimedFlyModule {

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

    /**
     * Gets called when the module gets enabled.
     * Every implementation must have this method.
     */
    public abstract void onModuleEnable();

    /**
     * Gets called when the module gets disabled.
     * Every implementation must have this method.
     */
    public abstract void onModuleDisable();
}
