package bypass.command;

import bypass.module.exploits.Denicker;
import net.minecraft.util.EnumChatFormatting;

public final class HelpCommand extends Command {
   private static final long d = 104505031812019L;

   public HelpCommand(String string, String string2) {
      super(string, string2);
   }

   @Override
   public void runCommand(String[] strings) {
      long l = d ^ 42894612555310L;
      long m = l ^ 38175528195227L;
      Denicker.nicks.clear();

      for (CommandInfo commandInfo : client.getCommandManager().getList()) {
         if (commandInfo instanceof Command) {
            chat.D(false, false).i(".").i(commandInfo.getName()).M(EnumChatFormatting.DARK_PURPLE).i(" - ").M(EnumChatFormatting.WHITE).i(commandInfo.getUsage()).N().b();
         }
      }
   }
}
