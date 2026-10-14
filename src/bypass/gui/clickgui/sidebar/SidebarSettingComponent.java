package bypass.gui.clickgui.sidebar;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.gui.component.Container;
import bypass.setting.Setting;

public abstract class SidebarSettingComponent extends Container {
   private Setting n;
   public int X;
   public Animation B = new TimedAnimation().setDuration(200).setEasing(Easing.LINEAR);
   private boolean h;

   public SidebarSettingComponent(ModuleButton moduleButton, Setting setting, float f, float g, int i, int j) {
      super(setting.getName(), f, g, i, j);
      this.f(moduleButton);
      this.n = setting;
   }

   public ModuleButton G() {
      return (ModuleButton)this.O();
   }

   public Setting H() {
      return this.n;
   }

   @Override
   public void W(int i, int j, int k) {
      ModuleButton moduleButton = this.G();
      int l = moduleButton.A + k;
      int m = moduleButton.n().t().Y() - moduleButton.v();
      if (l > 25) {
         moduleButton.A = 25;
      } else if (l >= m) {
         moduleButton.A += k;
         super.W(i, j, k);
      }
   }

   public boolean y() {
      if (this.h) {
         return true;
      }

      if (this.G() == null) {
         return true;
      }

      CategoryTab categoryTab = this.G().n();
      int i = this.G().A;
      int j = categoryTab.t().Y() - this.G().F(this);
      return j >= i - 10 && categoryTab.O().E_float() + 20.0F < this.E_float();
   }

   public void G(boolean bl) {
      this.h = bl;
   }
}
