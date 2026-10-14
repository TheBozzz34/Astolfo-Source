package bypass.command;

import bypass.chat.ChatManager;

public class ChatCommand extends Command {
   public ChatCommand(String string, String string2) {
      super(string, string2);
   }

   @Override
   public void runCommand(String[] strings) {
      switch (strings.length) {
         case 2:
            if (strings[1].equalsIgnoreCase("enabled")) {
               ChatManager.D(!ChatManager.g());
               chat.I("Astolfo Chat has been set to " + ChatManager.g());
            } else if (strings[1].equals("pings")) {
               ChatManager.x(!ChatManager.X());
               chat.I("Astolfo Chat pings has been set to " + ChatManager.X());
            }
            break;
         default:
            chat.I(this.getName() + ": " + this.getUsage());
      }
   }
}
