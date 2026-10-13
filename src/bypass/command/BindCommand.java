package bypass.command;

import bypass.module.Module;
import bypass.module.ModuleInfo;
import bypass.module.hidden.GUI;
import java.util.Arrays;
import org.lwjgl.input.Keyboard;

public final class BindCommand extends Command {
   private static final long d = 114879399539906L;

   public BindCommand(String var1, String var2) {
      super(var1, var2);
      this.setAliases(Arrays.asList("b", "key"));
   }

   @Override
   public void runCommand(String[] var1) {
      long var2 = d ^ 18088903160159L;
      long var4 = var2 ^ 87407226648622L;
      if (var1.length == 4 && var1[1].equalsIgnoreCase("add")) {
         Module var10 = client.getModuleManager().getModule(var1[2]);
         if (var10 == null) {
            chat.P(String.format("Module %s not found.", var1[2]));
            return;
         }

         int var11 = Keyboard.getKeyIndex(var1[3].toUpperCase());
         var10.setKey(var11);
         chat.I(String.format("%s was bound to %s", var10.getName(), Keyboard.getKeyName(var10.getKey())));
      } else if (var1.length == 3 && var1[1].equalsIgnoreCase("del")) {
         Module var9 = client.getModuleManager().getModule(var1[2]);
         if (var9 == null) {
            chat.P(String.format("Module %s not found.", var1[2]));
            return;
         }

         var9.setKey(0);
         chat.I(String.format("%s keybind was deleted.", var9.getName()));
      } else if (var1.length == 2 && var1[1].equalsIgnoreCase("clear")) {
         for (ModuleInfo var8 : client.getModuleManager().getList()) {
            if (var8 instanceof Module && !(var8 instanceof GUI)) {
               ((Module)var8).setKey(0);
            }
         }

         chat.I("Cleared all module keybinds");
      } else {
         chat.I(this.getName() + ": " + this.getUsage());
      }
   }
}
