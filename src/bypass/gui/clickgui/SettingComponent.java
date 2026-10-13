package bypass.gui.clickgui;

import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;

public abstract class SettingComponent extends Component {
   private Setting C;
   private Container f;

   public SettingComponent(Container var1, Setting var2, int var3, int var4) {
      super(var2.getName(), 0.0F, 0.0F, var3, var4);
      this.f = var1;
      this.C = var2;
   }

   public boolean B() {
      return this.C instanceof BooleanSetting;
   }

   public boolean K() {
      return this.C instanceof NumberSetting;
   }

   public Setting t() {
      return this.C;
   }

   public Container I() {
      return this.f;
   }
}
