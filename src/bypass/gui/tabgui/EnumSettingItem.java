package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.setting.EnumSetting;

public class EnumSettingItem extends TabGuiItem {
   private final EnumSetting z;
   private static final long b = 95790679043354L;

   public EnumSettingItem(TabGui.Panel panel, EnumSetting enumSetting) {
      super(panel);
      this.z = enumSetting;
   }

   @Override
   public String L() {
      return this.z.getName() + ": §7" + this.z.getValue();
   }

   @Override
   public void onPress() {
      Enum[] enums = (Enum[])this.z.getValue().getClass().getEnumConstants();
      int i = this.O();
      i++;
      if (i >= enums.length || i < 0) {
         i = 0;
      }

      this.z.setValue(enums[i]);
   }

   @Override
   public void onExpand() {
      this.onPress();
   }

   private int O() {
      Enum[] enums = (Enum[])this.z.getValue().getClass().getEnumConstants();

      for (int i = 0; i < enums.length; i++) {
         if (enums[i] == this.z.getValue()) {
            return i;
         }
      }

      return -1;
   }
}
