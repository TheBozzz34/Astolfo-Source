package bypass.gui.clickgui;

import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;

public abstract class SettingComponent extends Component {
   private Setting C;
   private Container f;

   public SettingComponent(Container container, Setting setting, int i, int j) {
      super(setting.getName(), 0.0F, 0.0F, i, j);
      this.f = container;
      this.C = setting;
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
