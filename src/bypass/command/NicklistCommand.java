package bypass.command;

import bypass.module.exploits.Denicker;
import net.minecraft.util.EnumChatFormatting;

public final class NicklistCommand extends Command {
   private static final long d = 107110009519772L;

   public NicklistCommand(String string, String string2) {
      super(string, string2);
   }

   @Override
   public void runCommand(String[] strings) {
      Denicker.nicks
         .forEach((string, string2) -> chat.D(false, false).i("> ").i(string).M(EnumChatFormatting.GRAY).i(": ").M(EnumChatFormatting.WHITE).i(string2).N().b());
   }
}
