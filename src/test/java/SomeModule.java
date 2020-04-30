import me.jackscode.timedfly.api.Module;
import me.jackscode.timedfly.exceptions.CommandException;

public class SomeModule extends Module {

    @Override
    public void onModuleEnable() {
        try {
            this.registerCommands(
                    new SomeCommand(),
                    new ToggleModule(this.getModuleHandler(), this.getPlugin()),
                    new TestPlayerCommand()
            );
        } catch (CommandException e) {
            e.printStackTrace();
        }
        System.out.println(this.getModuleDescription().getAuthors());
    }

    @Override
    public void onModuleDisable() {
        System.out.println("NOW THIS WORKS????");
    }
}
