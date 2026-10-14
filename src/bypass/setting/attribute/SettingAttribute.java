package bypass.setting.attribute;

import bypass.event.Cancellable;

public class SettingAttribute implements Cancellable {
   private boolean D;
   private AttributePriority T;
   private static final long a = 28686905284496L;

   public SettingAttribute(AttributePriority attributePriority) {
      this.setPriority(attributePriority);
   }

   @Override
   public void cancel() {
      this.D = true;
   }

   @Override
   public boolean isCancelled() {
      return this.D;
   }

   @Override
   public void setCancelled(boolean bl) {
      this.D = bl;
   }

   public AttributePriority getPriority() {
      return this.T;
   }

   public void setPriority(AttributePriority attributePriority) {
      this.T = attributePriority;
   }
}
