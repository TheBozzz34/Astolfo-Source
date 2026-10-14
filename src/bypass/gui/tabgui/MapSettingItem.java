package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.NumberSetting;

public class MapSettingItem extends TabGuiItem {
   private final MapSetting O;
   private static final long b = 35034049222198L;

   public MapSettingItem(TabGui.Panel panel, MapSetting mapSetting) {
      super(panel);
      this.O = mapSetting;
   }

   @Override
   public String L() {
      return "§f" + this.O.getName() + "...";
   }

   @Override
   public void onPress() {
      TabGui.Panel panel = new TabGui.Panel(this.s().getTabGui());
      this.O.getValue().forEach((string, setting) -> {
         if (setting instanceof BooleanSetting) {
            if (!setting.getName().equals("Hide")) {
               panel.addItem(new BooleanSettingItem(panel, (BooleanSetting)setting));
            }
         } else if (setting instanceof NumberSetting) {
            panel.addItem(new NumberSettingItem(panel, (NumberSetting)setting));
         } else if (setting instanceof EnumSetting) {
            panel.addItem(new EnumSettingItem(panel, (EnumSetting)setting));
         }
      });
      this.s().getTabGui().s(panel);
   }

   @Override
   public void onExpand() {
      this.onPress();
   }
}
