package bypass.command;

import bypass.auth.DisconnectHandler;
import bypass.util.Stopwatch;

public class BansCommand extends Command {
   public static int p;
   private final Stopwatch S = new Stopwatch();
   private static final long d = 87134111985292L;
   private static final long h = 8466051079300186124L;

   public BansCommand(String var1, String var2) {
      super(var1, var2);
   }

   @Override
   public void runCommand(String[] var1) {
      if (DisconnectHandler.disconnected) {
         chat.f("You are disconnected");
      } else {
         if (var1.length == 3) {
            if (!this.S.hasElapsed(5000.0F)) {
               chat.I("Wait " + Math.round(5.0F - (float)this.S.getElapsed() / 1000.0F) + "s before using this command again");
               return;
            }

            int var5 = l(var1[1].toLowerCase());
            if (!var1[1].equalsIgnoreCase("all") && var5 == 0) {
               chat.I("Gamemode is invalid, defaulting to all");
            }

            try {
               p = Integer.parseInt(var1[2]);
               if (p <= 0 || p > (int)h) {
                  chat.f("Invalid hour");
                  return;
               }

               this.S.reset();
            } catch (NumberFormatException var7) {
               chat.f("Invalid hour");
            }
         } else {
            chat.I(this.getName() + ": " + this.getUsage());
         }
      }
   }

   public static int l(String var0) {
      switch (var0) {
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

   public static String G(int var0) {
      switch (var0) {
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
