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
   private static final long f = 139085029211665L;

   public SidebarSettingComponent(ModuleButton var1, Setting var2, float var3, float var4, int var5, int var6) {
      super(var2.getName(), var3, var4, var5, var6);
      this.f(var1);
      this.n = var2;
   }

   public ModuleButton G() {
      return (ModuleButton)this.O();
   }

   public Setting H() {
      return this.n;
   }

   @Override
   public void W(int var1, int var2, int var3) {
      ModuleButton var7 = this.G();
      int var8 = var7.A + var3;
      int var9 = var7.n().t().Y() - var7.v();
      if (var8 > 25) {
         var7.A = 25;
      } else if (var8 >= var9) {
         var7.A += var3;
         super.W(var1, var2, var3);
      }
   }

   public boolean y() {
      if (this.h) {
         return true;
      }

      if (this.G() == null) {
         return true;
      }

      CategoryTab var4 = this.G().n();
      int var5 = this.G().A;
      int var6 = var4.t().Y() - this.G().F(this);
      return var6 >= var5 - 10 && var4.O().E_float() + 20.0F < this.E_float();
   }

   public void G(boolean var1) {
      this.h = var1;
   }
}
