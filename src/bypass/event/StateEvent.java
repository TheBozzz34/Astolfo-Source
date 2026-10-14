package bypass.event;

public abstract class StateEvent implements Event {
   public EventState state;
   private static final long a = 48377300039960L;

   public StateEvent(EventState eventState) {
      this.state = eventState;
   }

   public EventState getState() {
      return this.state;
   }

   public void setState(EventState eventState) {
      this.state = eventState;
   }
}
