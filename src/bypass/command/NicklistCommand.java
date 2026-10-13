package bypass.command;

import bypass.module.exploits.Denicker;
import net.minecraft.util.EnumChatFormatting;

public final class NicklistCommand extends Command {
   private static final long d = 107110009519772L;

   public NicklistCommand(String var1, String var2) {
      super(var1, var2);
   }

   @Override
   public void runCommand(String[] var1) {
      Denicker.nicks
         .forEach((var0, var1x) -> chat.D(false, false).i("> ").i(var0).M(EnumChatFormatting.GRAY).i(": ").M(EnumChatFormatting.WHITE).i(var1x).N().b());
   }
}
