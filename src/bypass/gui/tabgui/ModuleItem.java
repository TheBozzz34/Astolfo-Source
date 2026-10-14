package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.NumberSetting;

public class ModuleItem extends TabGuiItem {
   private final Module U;
   private static final long b = 49190119730991L;

   public ModuleItem(TabGui.Panel panel, Module module) {
      super(panel);
      this.U = module;
   }

   public Module c() {
      return this.U;
   }

   @Override
   public String L() {
      return (this.U.isEnabled() ? "§f" : "§7") + this.U.getName();
   }

   @Override
   public void onPress() {
      this.U.toggle();
   }

   @Override
   public void onExpand() {
      TabGui.Panel panel = new TabGui.Panel(this.s().getTabGui());
      this.U.getSettings().forEach(setting -> {
         if (setting instanceof BooleanSetting) {
            BooleanSetting booleanSetting = (BooleanSetting)setting;
            if (!setting.getName().equals("Hide")) {
               panel.addItem(new BooleanSettingItem(panel, booleanSetting));
            }
         } else if (setting instanceof NumberSetting) {
            NumberSetting numberSetting = (NumberSetting)setting;
            panel.addItem(new NumberSettingItem(panel, numberSetting));
         } else if (setting instanceof MapSetting) {
            MapSetting mapSetting = (MapSetting)setting;
            panel.addItem(new MapSettingItem(panel, mapSetting));
         } else if (setting instanceof EnumSetting) {
            panel.addItem(new EnumSettingItem(panel, (EnumSetting)setting));
         }
      });
      if (panel.getItems().size() != 0) {
         panel.getTabGui().s(panel);
      }
   }
}
