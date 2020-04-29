import me.jackscode.timedfly.api.Module;
import me.jackscode.timedfly.handlers.CommandHandler;
import me.jackscode.timedfly.handlers.ModuleHandler;
import org.junit.Test;

import java.io.File;

public class SomeTest {

    @Test
    public void main() {
        File moduleFile = new File(
                "D:\\NextCloud\\Private\\Documents\\Projects\\Java\\TimedFly5\\out\\artifacts\\TestModule/TestModule.jar"
        );
        Module module =  new ModuleHandler(new CommandHandler(), null).enableModule(moduleFile);

        System.out.println(module.getModuleDescription().getName());
    }


}
