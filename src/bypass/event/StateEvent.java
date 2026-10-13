package bypass.event;

public abstract class StateEvent implements Event {
   public EventState state;
   private static final long a = 48377300039960L;

   public StateEvent(EventState var1) {
      this.state = var1;
   }

   public EventState getState() {
      return this.state;
   }

   public void setState(EventState var1) {
      this.state = var1;
   }
}
