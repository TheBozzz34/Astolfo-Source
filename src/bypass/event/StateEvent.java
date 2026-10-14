package bypass.event;

public abstract class StateEvent implements Event {
   public EventState state;

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
