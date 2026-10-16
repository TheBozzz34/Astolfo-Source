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
               ChatManager.setChatEnabled(!ChatManager.isChatEnabled());
               chat.print("Astolfo Chat has been set to " + ChatManager.isChatEnabled());
            } else if (strings[1].equals("pings")) {
               ChatManager.setPingsEnabled(!ChatManager.isPingsEnabled());
               chat.print("Astolfo Chat pings has been set to " + ChatManager.isPingsEnabled());
            }
            break;
         default:
            chat.print(this.getName() + ": " + this.getUsage());
      }
   }
}
