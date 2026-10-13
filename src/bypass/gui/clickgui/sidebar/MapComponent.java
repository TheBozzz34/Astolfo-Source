package bypass.gui.clickgui.sidebar;

import bypass.Astolfo;
import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.gui.component.Element;
import bypass.render.RenderUtil;
import bypass.setting.MapSetting;
import java.awt.Color;

public class MapComponent extends SidebarSettingComponent {
   private Animation w = new TimedAnimation().setDuration(450).setEasing(Easing.LINEAR).forwards().start();
   private final MapSetting K;
   private boolean C;
   private int u = 25;
   private boolean Z;
   private static final long d = 87039549887153L;

   public MapComponent(ModuleButton var1, MapSetting var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.f(var1);
      this.K = var2;
   }

   @Override
   public void l(int var1) {
      if (var1 == 0 && this.y()) {
         if (this.G().A() != null) {
            if (this.G().A() != this) {
               this.G().D(this);
            } else {
               this.G().D(null);
            }
         } else {
            this.G().D(this);
         }
      }
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = d ^ 96923443186883L;
      long var5 = var3 ^ 8972929756414L;
      if (!this.w.isRunning() && (this.G(var1, var2) || this.G().A() == this)) {
         if (this.Z) {
            this.w.forwards().start();
         } else {
            this.w.backwards().start();
         }

         this.Z = !this.Z;
      }

      if (!this.G(var1, var2) && this.G().A() != this) {
         this.w.forwards();
      }

      if (this.X > 40) {
         client.getCustomFontRenderer().drawString(this.K.getName(), this.M(), this.E_float() - 2.0F, new Color(255, 255, 255, this.X).getRGB());
      }

      int var8 = Astolfo.INSTANCE.getSidebarGui().U();
      RenderUtil.drawArrow(
         this.M() + this.x() - 8.0F + this.w.getValue(-8.0, 0.0),
         this.E_float(),
         5.0,
         true,
         this.G().A() == this ? var8 : new Color(255, 255, 255, this.X).getRGB()
      );
      Sidebar var9 = this.G().n().t();
      int var10 = this.u;
      if (!this.C && this.G().h() == this && this.G().A() == null
         || this.G().h() == null && this.G().A() == this
         || this.G().h() != this && this.G().A() == this) {
         for (Element var15 : this.M_ArrayList()) {
            if (var15 instanceof SidebarSettingComponent) {
               SidebarSettingComponent var16 = (SidebarSettingComponent)var15;
               var16.r(this.M() + 155.0F, var9.E_float() + var10);
               var16.render(var1, var2);
               if (!var16.B.isRunning()) {
                  var16.B.forwards().start();
               }

               var16.X = (int)var16.B.getValue(0.0, 255.0);
               var10 += 15;
            }
         }
      } else {
         for (Element var12 : this.M_ArrayList()) {
            if (var12 instanceof SidebarSettingComponent) {
               SidebarSettingComponent var13 = (SidebarSettingComponent)var12;
               if (!var13.B.isRunning()) {
                  var13.B.backwards().start();
               }

               var13.X = (int)var13.B.getValue(0.0, 255.0);
            }
         }
      }

      this.C = this.G().n().t().x() == 270;
   }

   public MapSetting X() {
      return this.K;
   }
}
