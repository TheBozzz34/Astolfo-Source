package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.setting.EnumSetting;

public class EnumSettingItem extends TabGuiItem {
   private final EnumSetting setting;

   public EnumSettingItem(TabGui.Panel panel, EnumSetting enumSetting) {
      super(panel);
      this.setting = enumSetting;
   }

   @Override
   public String getDisplayText() {
      return this.setting.getName() + ": §7" + this.setting.getValue();
   }

   @Override
   public void onPress() {
      Enum[] enums = (Enum[])this.setting.getValue().getClass().getEnumConstants();
      int i = this.getCurrentIndex();
      i++;
      if (i >= enums.length || i < 0) {
         i = 0;
      }

      this.setting.setValue(enums[i]);
   }

   @Override
   public void onExpand() {
      this.onPress();
   }

   private int getCurrentIndex() {
      Enum[] enums = (Enum[])this.setting.getValue().getClass().getEnumConstants();

      for (int i = 0; i < enums.length; i++) {
         if (enums[i] == this.setting.getValue()) {
            return i;
         }
      }

      return -1;
   }
}
