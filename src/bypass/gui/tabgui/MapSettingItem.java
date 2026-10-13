package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.NumberSetting;

public class MapSettingItem extends TabGuiItem {
   private final MapSetting O;
   private static final long b = 35034049222198L;

   public MapSettingItem(TabGui.Panel var1, MapSetting var2) {
      super(var1);
      this.O = var2;
   }

   @Override
   public String L() {
      return "§f" + this.O.getName() + "...";
   }

   @Override
   public void onPress() {
      TabGui.Panel var4 = new TabGui.Panel(this.s().getTabGui());
      this.O.getValue().forEach((var1, var2) -> {
         if (var2 instanceof BooleanSetting) {
            if (!var2.getName().equals("Hide")) {
               var4.addItem(new BooleanSettingItem(var4, (BooleanSetting)var2));
            }
         } else if (var2 instanceof NumberSetting) {
            var4.addItem(new NumberSettingItem(var4, (NumberSetting)var2));
         } else if (var2 instanceof EnumSetting) {
            var4.addItem(new EnumSettingItem(var4, (EnumSetting)var2));
         }
      });
      this.s().getTabGui().s(var4);
   }

   @Override
   public void onExpand() {
      this.onPress();
   }
}
