package bypass.command;

import bypass.module.exploits.Denicker;
import net.minecraft.util.EnumChatFormatting;

public final class HelpCommand extends Command {
   private static final long d = 104505031812019L;

   public HelpCommand(String var1, String var2) {
      super(var1, var2);
   }

   @Override
   public void runCommand(String[] var1) {
      long var2 = d ^ 42894612555310L;
      long var4 = var2 ^ 38175528195227L;
      Denicker.nicks.clear();

      for (CommandInfo var8 : client.getCommandManager().getList()) {
         if (var8 instanceof Command) {
            chat.D(false, false).i(".").i(var8.getName()).M(EnumChatFormatting.DARK_PURPLE).i(" - ").M(EnumChatFormatting.WHITE).i(var8.getUsage()).N().b();
         }
      }
   }
}
