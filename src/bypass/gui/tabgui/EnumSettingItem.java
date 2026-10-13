package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.setting.EnumSetting;

public class EnumSettingItem extends TabGuiItem {
   private final EnumSetting z;
   private static final long b = 95790679043354L;

   public EnumSettingItem(TabGui.Panel var1, EnumSetting var2) {
      super(var1);
      this.z = var2;
   }

   @Override
   public String L() {
      return this.z.getName() + ": §7" + this.z.getValue();
   }

   @Override
   public void onPress() {
      Enum[] var5 = (Enum[])this.z.getValue().getClass().getEnumConstants();
      int var6 = this.O();
      var6++;
      if (var6 >= var5.length || var6 < 0) {
         var6 = 0;
      }

      this.z.setValue(var5[var6]);
   }

   @Override
   public void onExpand() {
      this.onPress();
   }

   private int O() {
      Enum[] var4 = (Enum[])this.z.getValue().getClass().getEnumConstants();

      for (int var5 = 0; var5 < var4.length; var5++) {
         if (var4[var5] == this.z.getValue()) {
            return var5;
         }
      }

      return -1;
   }
}
