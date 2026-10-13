package bypass.command;

import bypass.chat.ChatManager;

public class ChatCommand extends Command {
   private static final long d = 8332550716453L;

   public ChatCommand(String var1, String var2) {
      super(var1, var2);
   }

   @Override
   public void runCommand(String[] var1) {
      switch (var1.length) {
         case 2:
            if (var1[1].equalsIgnoreCase("enabled")) {
               ChatManager.D(!ChatManager.g());
               chat.I("Astolfo Chat has been set to " + ChatManager.g());
            } else if (var1[1].equals("pings")) {
               ChatManager.x(!ChatManager.X());
               chat.I("Astolfo Chat pings has been set to " + ChatManager.X());
            }
            break;
         default:
            chat.I(this.getName() + ": " + this.getUsage());
      }
   }
}
