package bypass.gui.clickgui;

import bypass.gui.component.Container;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.Setting;

public abstract class SettingContainer extends Container {
   private Setting A;
   private Container H;

   public SettingContainer(Container var1, Setting var2, int var3, int var4) {
      super(var2.getName(), 0.0F, 0.0F, var3, var4);
      this.H = var1;
      this.A = var2;
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
