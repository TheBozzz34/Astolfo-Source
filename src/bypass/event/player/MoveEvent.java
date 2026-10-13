package bypass.event.player;

import bypass.event.Event;
import bypass.event.EventState;

public class MoveEvent implements Event {
   private double x;
   private double y;
   private double z;
   private EventState state;

   public MoveEvent(double var1, double var3, double var5) {
      this.state = EventState.PRE;
      this.x = var1;
      this.y = var3;
      this.z = var5;
   }

   public MoveEvent() {
      this.state = EventState.POST;
   }

   public EventState getState() {
      return this.state;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public void setX(double var1) {
      this.x = var1;
   }

   public void setY(double var1) {
      this.y = var1;
   }

   public void setZ(double var1) {
      this.z = var1;
   }
}
