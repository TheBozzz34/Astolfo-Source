package bypass.command;

import java.util.Collections;

public final class HclipCommand extends Command {
   private static final long d = 48030420445214L;

   public HclipCommand(String var1, String var2) {
      super(var1, var2);
      this.setAliases(Collections.singletonList("hc"));
   }

   @Override
   public void runCommand(String[] var1) {
      if (var1.length > 1) {
         double var5;
         try {
            var5 = Double.parseDouble(var1[1]);
         } catch (NumberFormatException var15) {
            chat.P("\"" + var1[1] + "\" is not a number.");
            return;
         }

         double var7 = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
         double var9 = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
         double var11 = 1.0 * var5 * var7 + 0.0 * var5 * var9;
         double var13 = 1.0 * var5 * var9 - 0.0 * var5 * var7;
         mc.thePlayer.setPosition(mc.thePlayer.posX + var11, mc.thePlayer.posY, mc.thePlayer.posZ + var13);
      } else {
         chat.I("Hclip: " + this.getUsage());
      }
   }
}
