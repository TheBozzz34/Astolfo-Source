package bypass.setting.attribute;

import bypass.event.Cancellable;

public abstract class ChangeListenerAttribute extends SettingAttribute implements Cancellable {
   private boolean listenerCancelled;

   public ChangeListenerAttribute() {
      super(AttributePriority.HIGH);
   }

   @Override
   public void cancel() {
      this.listenerCancelled = true;
   }

   @Override
   public boolean isCancelled() {
      return this.listenerCancelled;
   }

   @Override
   public void setCancelled(boolean bl) {
      this.listenerCancelled = bl;
   }

   public abstract void onChange(SettingPart settingPart);
}
