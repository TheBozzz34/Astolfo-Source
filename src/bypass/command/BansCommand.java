package bypass.command;

import bypass.auth.DisconnectHandler;
import bypass.util.Stopwatch;

public class BansCommand extends Command {
   public static int p;
   private final Stopwatch S = new Stopwatch();

   public BansCommand(String string, String string2) {
      super(string, string2);
   }

   @Override
   public void runCommand(String[] strings) {
      if (DisconnectHandler.disconnected) {
         chat.f("You are disconnected");
      } else {
         if (strings.length == 3) {
            if (!this.S.hasElapsed(5000.0F)) {
               chat.I("Wait " + Math.round(5.0F - (float)this.S.getElapsed() / 1000.0F) + "s before using this command again");
               return;
            }

            int i = l(strings[1].toLowerCase());
            if (!strings[1].equalsIgnoreCase("all") && i == 0) {
               chat.I("Gamemode is invalid, defaulting to all");
            }

            try {
               p = Integer.parseInt(strings[2]);
               if (p <= 0 || p > 12) {
                  chat.f("Invalid hour");
                  return;
               }

               this.S.reset();
            } catch (NumberFormatException numberFormatException) {
               chat.f("Invalid hour");
            }
         } else {
            chat.I(this.getName() + ": " + this.getUsage());
         }
      }
   }

   public static int l(String string) {
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

   public static String G(int i) {
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
