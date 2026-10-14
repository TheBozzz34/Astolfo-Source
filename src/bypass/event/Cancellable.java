package bypass.event;

public interface Cancellable {
   void cancel();

   boolean isCancelled();

   void setCancelled(boolean bl);
}
