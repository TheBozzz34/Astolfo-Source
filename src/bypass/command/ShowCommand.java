package bypass.command;

import bypass.module.Module;
import bypass.module.ModuleInfo;
import java.util.Collections;

public final class ShowCommand extends Command {
   private static final long d = 76152120535236L;

   public ShowCommand(String var1, String var2) {
      super(var1, var2);
      this.setAliases(Collections.singletonList("s"));
   }

   @Override
   public void runCommand(String[] var1) {
      long var2 = d ^ 67389571951961L;
      long var4 = var2 ^ 87407226648622L;
      if (var1.length == 2) {
         if (var1[1].equalsIgnoreCase("all")) {
            for (ModuleInfo var8 : client.getModuleManager().getList()) {
               if (var8 instanceof Module) {
                  Module var9 = (Module)var8;
                  var9.setHidden(true);
               }
            }
         } else {
            ModuleInfo var10 = client.getModuleManager().getByName(var1[1]);
            if (var10 instanceof Module) {
               ((Module)var10).setHidden(true);
            } else {
               chat.f("Module not found");
            }
         }
      }
   }
}
