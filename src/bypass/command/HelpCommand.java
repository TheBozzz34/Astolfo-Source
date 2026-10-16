package bypass.command;

import bypass.module.exploits.Denicker;
import net.minecraft.util.EnumChatFormatting;

public final class HelpCommand extends Command {
   public HelpCommand(String string, String string2) {
      super(string, string2);
   }

   @Override
   public void runCommand(String[] strings) {
      Denicker.nicks.clear();

      for (CommandInfo commandInfo : client.getCommandManager().getList()) {
         if (commandInfo instanceof Command) {
            chat.createBuilder(false, false)
               .append(".")
               .append(commandInfo.getName())
               .color(EnumChatFormatting.DARK_PURPLE)
               .append(" - ")
               .color(EnumChatFormatting.WHITE)
               .append(commandInfo.getUsage())
               .build()
               .send();
         }
      }
   }
}
