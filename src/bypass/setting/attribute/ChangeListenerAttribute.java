package bypass.setting.attribute;

import bypass.event.Cancellable;

public abstract class ChangeListenerAttribute extends SettingAttribute implements Cancellable {
   private boolean i;

   public ChangeListenerAttribute() {
      super(AttributePriority.HIGH);
   }

   @Override
   public void cancel() {
      this.i = true;
   }

   @Override
   public boolean isCancelled() {
      return this.i;
   }

   @Override
   public void setCancelled(boolean bl) {
      this.i = bl;
   }

   public abstract void onChange(SettingPart settingPart);
}
