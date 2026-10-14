package bypass.command;

import java.util.Collections;

public final class HclipCommand extends Command {
   public HclipCommand(String string, String string2) {
      super(string, string2);
      this.setAliases(Collections.singletonList("hc"));
   }

   @Override
   public void runCommand(String[] strings) {
      if (strings.length > 1) {
         double d;
         try {
            d = Double.parseDouble(strings[1]);
         } catch (NumberFormatException numberFormatException) {
            chat.P("\"" + strings[1] + "\" is not a number.");
            return;
         }

         double e = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
         double d2 = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0F));
         double d3 = 1.0 * d * e + 0.0 * d * d2;
         double d4 = 1.0 * d * d2 - 0.0 * d * e;
         mc.thePlayer.setPosition(mc.thePlayer.posX + d3, mc.thePlayer.posY, mc.thePlayer.posZ + d4);
      } else {
         chat.I("Hclip: " + this.getUsage());
      }
   }
}
