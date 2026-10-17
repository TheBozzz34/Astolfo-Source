package bypass.command;

import java.util.Collections;

public final class VclipCommand extends Command {
   public VclipCommand(String string, String string2) {
      super(string, string2);
      this.setAliases(Collections.singletonList("clip"));
   }

   @Override
   public void runCommand(String[] strings) {
      if (strings.length > 1) {
         double d;
         try {
            d = Double.parseDouble(strings[1]);
         } catch (NumberFormatException numberFormatException) {
            chat.printWarning("\"" + strings[1] + "\" is not a number.");
            return;
         }

         mc.thePlayer.getEntityBoundingBox().offsetInPlace(0.0, d, 0.0);
         chat.printSuccess("Teleported " + (d != Double.NaN && d != 0.0 ? (d > 0.0 ? "up " : "down ") : "nowhere ") + d + " blocks");
      } else {
         chat.print("Vclip: " + this.getUsage());
      }
   }
}
