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

   public MapComponent(ModuleButton moduleButton, MapSetting mapSetting, int i, int j, int k, int l) {
      super(moduleButton, mapSetting, i, j, k, l);
      this.f(moduleButton);
      this.K = mapSetting;
   }

   @Override
   public void l(int i) {
      if (i == 0 && this.y()) {
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
   public void render(int i, int j) {
      if (!this.w.isRunning() && (this.G(i, j) || this.G().A() == this)) {
         if (this.Z) {
            this.w.forwards().start();
         } else {
            this.w.backwards().start();
         }

         this.Z = !this.Z;
      }

      if (!this.G(i, j) && this.G().A() != this) {
         this.w.forwards();
      }

      if (this.X > 40) {
         client.getCustomFontRenderer().drawString(this.K.getName(), this.M(), this.E_float() - 2.0F, new Color(255, 255, 255, this.X).getRGB());
      }

      int k = Astolfo.INSTANCE.getSidebarGui().U();
      RenderUtil.drawArrow(
         this.M() + this.x() - 8.0F + this.w.getValue(-8.0, 0.0),
         this.E_float(),
         5.0,
         true,
         this.G().A() == this ? k : new Color(255, 255, 255, this.X).getRGB()
      );
      Sidebar sidebar = this.G().n().t();
      int l = this.u;
      if (!this.C && this.G().h() == this && this.G().A() == null
         || this.G().h() == null && this.G().A() == this
         || this.G().h() != this && this.G().A() == this) {
         for (Element element : this.M_ArrayList()) {
            if (element instanceof SidebarSettingComponent) {
               SidebarSettingComponent sidebarSettingComponent = (SidebarSettingComponent)element;
               sidebarSettingComponent.r(this.M() + 155.0F, sidebar.E_float() + l);
               sidebarSettingComponent.render(i, j);
               if (!sidebarSettingComponent.B.isRunning()) {
                  sidebarSettingComponent.B.forwards().start();
               }

               sidebarSettingComponent.X = (int)sidebarSettingComponent.B.getValue(0.0, 255.0);
               l += 15;
            }
         }
      } else {
         for (Element element2 : this.M_ArrayList()) {
            if (element2 instanceof SidebarSettingComponent) {
               SidebarSettingComponent sidebarSettingComponent2 = (SidebarSettingComponent)element2;
               if (!sidebarSettingComponent2.B.isRunning()) {
                  sidebarSettingComponent2.B.backwards().start();
               }

               sidebarSettingComponent2.X = (int)sidebarSettingComponent2.B.getValue(0.0, 255.0);
            }
         }
      }

      this.C = this.G().n().t().x() == 270;
   }

   public MapSetting X() {
      return this.K;
   }
}
