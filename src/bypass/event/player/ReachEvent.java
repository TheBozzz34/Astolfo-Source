package bypass.event.player;

import bypass.event.Event;

public class ReachEvent implements Event {
   private double reach = 3.0;

   public void setReach(double var1) {
      this.reach = var1;
   }

   public double getReach() {
      return this.reach;
   }
}
