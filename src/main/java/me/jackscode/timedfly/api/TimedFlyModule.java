package me.jackscode.timedfly.api;

public abstract class TimedFlyModule {

    private TimedFlyModuleDescription moduleDescription;

    /**
     * Get the description of the module in different methods.
     *
     * @return The description of the module
     */
    public TimedFlyModuleDescription getModuleDescription() {
        return moduleDescription;
    }

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
