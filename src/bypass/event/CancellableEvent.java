package bypass.event;

public abstract class CancellableEvent implements Event, Cancellable {
   private boolean cancelled;

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean var1) {
      this.cancelled = var1;
   }

   @Override
   public void cancel() {
      this.cancelled = true;
   }
}
