package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.setting.BooleanSetting;

public class BooleanSettingItem extends TabGuiItem {
   private final BooleanSetting w;
   private static final long b = 112154253273414L;

   public BooleanSettingItem(TabGui.Panel panel, BooleanSetting booleanSetting) {
      super(panel);
      this.w = booleanSetting;
   }

   @Override
   public String L() {
      return (this.w.getValue() ? "§f" : "§7") + this.w.getName();
   }

   @Override
   public void onPress() {
      this.w.set(!this.w.getValue());
   }

   @Override
   public void onExpand() {
      this.onPress();
   }
}
