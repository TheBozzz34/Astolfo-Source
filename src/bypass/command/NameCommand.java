package bypass.command;

import net.minecraft.client.gui.GuiScreen;

public final class NameCommand extends Command {
   public NameCommand(String string, String string2) {
      super(string, string2);
   }

   @Override
   public void runCommand(String[] strings) {
      toasts.info("Your username has been copied to the clipboard");
      GuiScreen.setClipboardString(mc.session.getUsername());
   }
}
