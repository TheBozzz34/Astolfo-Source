package bypass.command;

import bypass.manager.Loadable;
import bypass.manager.Manager;

public class CommandManager extends Manager<CommandInfo> implements Loadable {
   private static final long a = 139592478139702L;

   @Override
   public void load() {
   }

   @Override
   public void a() {
   }

   public CommandInfo getCommand(String var1) {
      for (CommandInfo var7 : this.C) {
         if (var7 instanceof Command
            && (var7.getName().equalsIgnoreCase(var1) || ((Command)var7).getAliases() != null && ((Command)var7).getAliases().contains(var1))) {
            return var7;
         }
      }

      return null;
   }
}
