package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.NumberSetting;

public class ModuleItem extends TabGuiItem {
   private final Module module;

   public ModuleItem(TabGui.Panel panel, Module module) {
      super(panel);
      this.module = module;
   }

   public Module getModule() {
      return this.module;
   }

   @Override
   public String getDisplayText() {
      return (this.module.isEnabled() ? "§f" : "§7") + this.module.getName();
   }

   @Override
   public void onPress() {
      this.module.toggle();
   }

   @Override
   public void onExpand() {
      TabGui.Panel panel = new TabGui.Panel(this.getPanel().getTabGui());
      this.module.getSettings().forEach(setting -> {
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
         panel.getTabGui().addElement(panel);
      }
   }
}
