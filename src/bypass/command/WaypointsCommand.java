package bypass.command;

import bypass.module.visuals.Waypoints;
import java.util.Arrays;

public final class WaypointsCommand extends Command {
   private static final long d = 88386230118112L;

   public WaypointsCommand(String var1, String var2) {
      super(var1, var2);
      this.setAliases(Arrays.asList("wp", "waypoint"));
   }

   @Override
   public void runCommand(String[] var1) {
      try {
         if (var1.length > 1) {
            if (var1[1].equalsIgnoreCase("add")) {
               switch (var1.length) {
                  case 3:
                     Waypoints.X(var1[2], mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                     chat.R(
                        String.format(
                           "Successfully added %s as waypoint (%s, %s, %s)", var1[2], (int)mc.thePlayer.posX, (int)mc.thePlayer.posY, (int)mc.thePlayer.posZ
                        )
                     );
                     break;
                  case 6:
                     double var5 = Double.parseDouble(var1[3]);
                     double var7 = Double.parseDouble(var1[4]);
                     double var9 = Double.parseDouble(var1[5]);
                     Waypoints.X(var1[2], var5, var7, var9);
                     chat.R(String.format("Successfully added %s as waypoint (%s, %s, %s)", var1[2], (int)var5, (int)var7, (int)var9));
               }
            }

            if (var1[1].equalsIgnoreCase("remove")) {
               switch (var1.length) {
                  case 3:
                     chat.R(String.format("Successfully removed %s waypoint", var1[2]));
                     Waypoints.G(var1[2]);
               }
            }
         }

         if (var1.length == 1) {
            chat.I(String.format("%s:%s", this.getName(), this.getUsage()));
         }
      } catch (Exception var11) {
         chat.I(String.format("%s:%s", this.getName(), this.getUsage()));
      }
   }
}
