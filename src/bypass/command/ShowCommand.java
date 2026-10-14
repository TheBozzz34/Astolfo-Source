package bypass.command;

import bypass.module.Module;
import bypass.module.ModuleInfo;
import java.util.Collections;

public final class ShowCommand extends Command {
   private static final long d = 76152120535236L;

   public ShowCommand(String string, String string2) {
      super(string, string2);
      this.setAliases(Collections.singletonList("s"));
   }

   @Override
   public void runCommand(String[] strings) {
      long l = d ^ 67389571951961L;
      long m = l ^ 87407226648622L;
      if (strings.length == 2) {
         if (strings[1].equalsIgnoreCase("all")) {
            for (ModuleInfo moduleInfo : client.getModuleManager().getList()) {
               if (moduleInfo instanceof Module) {
                  Module module = (Module)moduleInfo;
                  module.setHidden(true);
               }
            }
         } else {
            ModuleInfo moduleInfo2 = client.getModuleManager().getByName(strings[1]);
            if (moduleInfo2 instanceof Module) {
               ((Module)moduleInfo2).setHidden(true);
            } else {
               chat.f("Module not found");
            }
         }
      }
   }
}
