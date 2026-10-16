package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.NumberSetting;

public class MapSettingItem extends TabGuiItem {
   private final MapSetting setting;

   public MapSettingItem(TabGui.Panel panel, MapSetting mapSetting) {
      super(panel);
      this.setting = mapSetting;
   }

   @Override
   public String getDisplayText() {
      return "§f" + this.setting.getName() + "...";
   }

   @Override
   public void onPress() {
      TabGui.Panel panel = new TabGui.Panel(this.getPanel().getTabGui());
      this.setting.getValue().forEach((string, setting2) -> {
         if (setting2 instanceof BooleanSetting) {
            if (!setting2.getName().equals("Hide")) {
               panel.addItem(new BooleanSettingItem(panel, (BooleanSetting)setting2));
            }
         } else if (setting2 instanceof NumberSetting) {
            panel.addItem(new NumberSettingItem(panel, (NumberSetting)setting2));
         } else if (setting2 instanceof EnumSetting) {
            panel.addItem(new EnumSettingItem(panel, (EnumSetting)setting2));
         }
      });
      this.getPanel().getTabGui().addElement(panel);
   }

   @Override
   public void onExpand() {
      this.onPress();
   }
}
