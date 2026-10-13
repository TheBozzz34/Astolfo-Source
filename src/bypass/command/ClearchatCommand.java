package bypass.command;

import java.util.Collections;

public final class ClearchatCommand extends Command {
   private static final long d = 87502805952414L;

   public ClearchatCommand(String var1, String var2) {
      super(var1, var2);
      this.setAliases(Collections.singletonList("clear"));
   }

   @Override
   public void runCommand(String[] var1) {
      mc.ingameGUI.getChatGUI().clearChatMessages();
   }
}
