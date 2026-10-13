package bypass.setting.attribute;

import bypass.event.Cancellable;

public class SettingAttribute implements Cancellable {
   private boolean D;
   private AttributePriority T;
   private static final long a = 28686905284496L;

   public SettingAttribute(AttributePriority var1) {
      this.setPriority(var1);
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
   public void setCancelled(boolean var1) {
      this.D = var1;
   }

   public AttributePriority getPriority() {
      return this.T;
   }

   public void setPriority(AttributePriority var1) {
      this.T = var1;
   }
}
