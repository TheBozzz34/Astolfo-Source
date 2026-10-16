package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.setting.BooleanSetting;

public class BooleanSettingItem extends TabGuiItem {
   private final BooleanSetting setting;

   public BooleanSettingItem(TabGui.Panel panel, BooleanSetting booleanSetting) {
      super(panel);
      this.setting = booleanSetting;
   }

   @Override
   public String getDisplayText() {
      return (this.setting.getValue() ? "§f" : "§7") + this.setting.getName();
   }

   @Override
   public void onPress() {
      this.setting.set(!this.setting.getValue());
   }

   @Override
   public void onExpand() {
      this.onPress();
   }
}
