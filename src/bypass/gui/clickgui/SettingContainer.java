package bypass.gui.clickgui;

import bypass.gui.component.Container;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.Setting;

public abstract class SettingContainer extends Container {
   private Setting setting;
   private Container parentContainer;

   public SettingContainer(Container container, Setting setting, int i, int j) {
      super(setting.getName(), 0.0F, 0.0F, i, j);
      this.parentContainer = container;
      this.setting = setting;
   }

   public Setting getSetting() {
      return this.setting;
   }

   public MapSetting getMapSetting() {
      return (MapSetting)this.setting;
   }

   public EnumSetting getEnumSetting() {
      return (EnumSetting)this.setting;
   }

   public Container getParent() {
      return this.parentContainer;
   }
}
