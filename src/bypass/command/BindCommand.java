package bypass.command;

import bypass.module.Module;
import bypass.module.ModuleInfo;
import bypass.module.hidden.GUI;
import java.util.Arrays;
import org.lwjgl.input.Keyboard;

public final class BindCommand extends Command {
   public BindCommand(String string, String string2) {
      super(string, string2);
      this.setAliases(Arrays.asList("b", "key"));
   }

   @Override
   public void runCommand(String[] strings) {
      if (strings.length == 4 && strings[1].equalsIgnoreCase("add")) {
         Module module2 = client.getModuleManager().getModule(strings[2]);
         if (module2 == null) {
            chat.printWarning(String.format("Module %s not found.", strings[2]));
            return;
         }

         int i = Keyboard.getKeyIndex(strings[3].toUpperCase());
         module2.setKey(i);
         chat.print(String.format("%s was bound to %s", module2.getName(), Keyboard.getKeyName(module2.getKey())));
      } else if (strings.length == 3 && strings[1].equalsIgnoreCase("del")) {
         Module module3 = client.getModuleManager().getModule(strings[2]);
         if (module3 == null) {
            chat.printWarning(String.format("Module %s not found.", strings[2]));
            return;
         }

         module3.setKey(0);
         chat.print(String.format("%s keybind was deleted.", module3.getName()));
      } else if (strings.length == 2 && strings[1].equalsIgnoreCase("clear")) {
         for (ModuleInfo moduleInfo : client.getModuleManager().getList()) {
            if (moduleInfo instanceof Module && !(moduleInfo instanceof GUI)) {
               ((Module)moduleInfo).setKey(0);
            }
         }

         chat.print("Cleared all module keybinds");
      } else {
         chat.print(this.getName() + ": " + this.getUsage());
      }
   }
}
