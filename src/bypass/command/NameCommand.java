package bypass.command;

import net.minecraft.client.gui.GuiScreen;

public final class NameCommand extends Command {
   private static final long d = 65129890115941L;

   public NameCommand(String var1, String var2) {
      super(var1, var2);
   }

   @Override
   public void runCommand(String[] var1) {
      toasts.D("Your username has been copied to the clipboard");
      GuiScreen.setClipboardString(mc.session.getUsername());
   }
}
