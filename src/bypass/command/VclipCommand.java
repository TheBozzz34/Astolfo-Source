package bypass.command;

import java.util.Collections;

public final class VclipCommand extends Command {
   private static final long d = 91260789774393L;

   public VclipCommand(String var1, String var2) {
      super(var1, var2);
      this.setAliases(Collections.singletonList("clip"));
   }

   @Override
   public void runCommand(String[] var1) {
      if (var1.length > 1) {
         double var5;
         try {
            var5 = Double.parseDouble(var1[1]);
         } catch (NumberFormatException var8) {
            chat.P("\"" + var1[1] + "\" is not a number.");
            return;
         }

         mc.thePlayer.getEntityBoundingBox().X(0.0, var5, 0.0);
         chat.R("Teleported " + (var5 != Double.NaN && var5 != 0.0 ? (var5 > 0.0 ? "up " : "down ") : "nowhere ") + var5 + " blocks");
      } else {
         chat.I("Vclip: " + this.getUsage());
      }
   }
}
