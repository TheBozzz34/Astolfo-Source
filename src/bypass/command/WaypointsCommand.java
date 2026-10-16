package bypass.command;

import bypass.module.visuals.Waypoints;
import java.util.Arrays;

public final class WaypointsCommand extends Command {
   public WaypointsCommand(String string, String string2) {
      super(string, string2);
      this.setAliases(Arrays.asList("wp", "waypoint"));
   }

   @Override
   public void runCommand(String[] strings) {
      try {
         if (strings.length > 1) {
            if (strings[1].equalsIgnoreCase("add")) {
               switch (strings.length) {
                  case 3:
                     Waypoints.addWaypoint(strings[2], mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                     chat.printSuccess(
                        String.format(
                           "Successfully added %s as waypoint (%s, %s, %s)", strings[2], (int)mc.thePlayer.posX, (int)mc.thePlayer.posY, (int)mc.thePlayer.posZ
                        )
                     );
                     break;
                  case 6:
                     double d = Double.parseDouble(strings[3]);
                     double e = Double.parseDouble(strings[4]);
                     double d2 = Double.parseDouble(strings[5]);
                     Waypoints.addWaypoint(strings[2], d, e, d2);
                     chat.printSuccess(String.format("Successfully added %s as waypoint (%s, %s, %s)", strings[2], (int)d, (int)e, (int)d2));
               }
            }

            if (strings[1].equalsIgnoreCase("remove")) {
               switch (strings.length) {
                  case 3:
                     chat.printSuccess(String.format("Successfully removed %s waypoint", strings[2]));
                     Waypoints.removeWaypoint(strings[2]);
               }
            }
         }

         if (strings.length == 1) {
            chat.print(String.format("%s:%s", this.getName(), this.getUsage()));
         }
      } catch (Exception exception) {
         chat.print(String.format("%s:%s", this.getName(), this.getUsage()));
      }
   }
}
