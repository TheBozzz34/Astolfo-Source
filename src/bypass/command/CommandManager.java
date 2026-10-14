package bypass.command;

import bypass.manager.Loadable;
import bypass.manager.Manager;

public class CommandManager extends Manager<CommandInfo> implements Loadable {
   @Override
   public void load() {
   }

   @Override
   public void a() {
   }

   public CommandInfo getCommand(String string) {
      for (CommandInfo commandInfo : this.C) {
         if (commandInfo instanceof Command
            && (commandInfo.getName().equalsIgnoreCase(string) || ((Command)commandInfo).getAliases() != null && ((Command)commandInfo).getAliases().contains(string))) {
            return commandInfo;
         }
      }

      return null;
   }
}
