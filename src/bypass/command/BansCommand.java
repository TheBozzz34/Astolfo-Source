package bypass.command;

import bypass.auth.DisconnectHandler;
import bypass.util.Stopwatch;

public class BansCommand extends Command {
   public static int hours;
   private final Stopwatch cooldown = new Stopwatch();

   public BansCommand(String string, String string2) {
      super(string, string2);
   }

   @Override
   public void runCommand(String[] strings) {
      if (DisconnectHandler.disconnected) {
         chat.printError("You are disconnected");
      } else {
         if (strings.length == 3) {
            if (!this.cooldown.hasElapsed(5000.0F)) {
               chat.print("Wait " + Math.round(5.0F - (float)this.cooldown.getElapsed() / 1000.0F) + "s before using this command again");
               return;
            }

            int i = getModeId(strings[1].toLowerCase());
            if (!strings[1].equalsIgnoreCase("all") && i == 0) {
               chat.print("Gamemode is invalid, defaulting to all");
            }

            try {
               hours = Integer.parseInt(strings[2]);
               if (hours <= 0 || hours > 12) {
                  chat.printError("Invalid hour");
                  return;
               }

               this.cooldown.reset();
            } catch (NumberFormatException numberFormatException) {
               chat.printError("Invalid hour");
            }
         } else {
            chat.print(this.getName() + ": " + this.getUsage());
         }
      }
   }

   public static int getModeId(String string) {
      switch (string) {
         case "sw":
            return 1;
         case "bw":
            return 2;
         case "uhc":
            return 3;
         case "mw":
            return 4;
         case "pit":
            return 5;
         default:
            return 0;
      }
   }

   public static String getModeName(int i) {
      switch (i) {
         case 1:
            return "sw";
         case 2:
            return "bw";
         case 3:
            return "uhc";
         case 4:
            return "mw";
         case 5:
            return "pit";
         default:
            return "all";
      }
   }
}
