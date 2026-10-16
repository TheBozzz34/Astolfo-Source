package bypass.setting.attribute;

import bypass.event.Cancellable;

public class SettingAttribute implements Cancellable {
   private boolean cancelled;
   private AttributePriority priority;

   public SettingAttribute(AttributePriority attributePriority) {
      this.setPriority(attributePriority);
   }

   @Override
   public void cancel() {
      this.cancelled = true;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean bl) {
      this.cancelled = bl;
   }

   public AttributePriority getPriority() {
      return this.priority;
   }

   public void setPriority(AttributePriority attributePriority) {
      this.priority = attributePriority;
   }
}
