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
            chat.D(false, false).i(".").i(commandInfo.getName()).M(EnumChatFormatting.DARK_PURPLE).i(" - ").M(EnumChatFormatting.WHITE).i(commandInfo.getUsage()).N().b();
         }
      }
   }
}
