package bypass.event.player;

import bypass.event.Event;
import bypass.event.EventState;

public class MoveEvent implements Event {
   private double x;
   private double y;
   private double z;
   private EventState state;

   public MoveEvent(double d, double d2, double d3) {
      this.state = EventState.PRE;
      this.x = d;
      this.y = d2;
      this.z = d3;
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

   public void setX(double d) {
      this.x = d;
   }

   public void setY(double d) {
      this.y = d;
   }

   public void setZ(double d) {
      this.z = d;
   }
}
