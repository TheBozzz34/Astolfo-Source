package bypass.gui.clickgui;

import bypass.gui.component.Container;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.Setting;

public abstract class SettingContainer extends Container {
   private Setting A;
   private Container H;

   public SettingContainer(Container container, Setting setting, int i, int j) {
      super(setting.getName(), 0.0F, 0.0F, i, j);
      this.H = container;
      this.A = setting;
   }

   public Setting r() {
      return this.A;
   }

   public MapSetting T() {
      return (MapSetting)this.A;
   }

   public EnumSetting Q() {
      return (EnumSetting)this.A;
   }

   public Container O() {
      return this.H;
   }
}
