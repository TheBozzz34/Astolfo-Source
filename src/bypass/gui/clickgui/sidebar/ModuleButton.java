package bypass.gui.clickgui.sidebar;

import bypass.Astolfo;
import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.module.Module;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.GuiMainMenu;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ModuleButton extends Container {
   public Animation j = new TimedAnimation().setDuration(200).setEasing(Easing.LINEAR);
   private Animation Y = new TimedAnimation().setDuration(100).setEasing(Easing.LINEAR);
   private Module H;
   private MapComponent d;
   private MapComponent C;
   public boolean f;
   public int A = 25;
   private boolean Q;
   public int P;

   public ModuleButton(CategoryTab categoryTab, Module module, int i, int j, int k, int l) {
      super(module.getName(), i, j, k, l);
      this.f(categoryTab);
      this.H = module;
   }

   @Override
   public void l(int i) {
      CategoryTab categoryTab = this.n();
      int k = categoryTab.O;
      int m = categoryTab.t().Y() - categoryTab.x(this);
      boolean bl = m >= k - 10 && categoryTab.O().E_float() - 10.0F < this.E_float();
      if (bl) {
         switch (i) {
            case 0:
               this.H.toggle();
               break;
            case 1:
               if (this.n().D() != null) {
                  if (this.n().D() != this) {
                     if (!this.H.getSettings().isEmpty()) {
                        this.n().j(this);
                     }
                  } else {
                     this.n().j(null);
                  }
               } else if (!this.H.getSettings().isEmpty()) {
                  this.n().j(this);
               }
               break;
            case 2:
               this.Q = true;
         }
      }
   }

   @Override
   public void render(int i, int j) {
      if (!this.Y.isRunning()) {
         if (this.G(i, j)) {
            this.Y.forwards().start();
         } else {
            this.Y.backwards().start();
         }
      }

      int k = (int)this.Y.getValue(0.0, 255.0);
      if (this.H.isEnabled() && this.P > 40) {
         RenderUtil.drawRect(this.M() - 6.0F, this.E_float() - 2.0F, this.M() - 5.0F, this.E_float() + 9.0F, new Color(255, 255, 255, this.P).getRGB());
      }

      if (this.P > 40) {
         client.getCustomFontRenderer().drawString(this.H.getName(), this.M(), this.E_float() - 2.0F, new Color(136, 141, 157, this.P).getRGB());
      }

      if (k > 40) {
         client.getCustomFontRenderer().drawString(this.H.getName(), this.M(), this.E_float() - 2.0F, new Color(255, 255, 255, k).getRGB());
      }

      int l = this.A;
      Sidebar sidebar = this.n().t();
      if (!this.f && this.n().S() == this && this.n().D() == null
         || this.n().S() == null && this.n().D() == this
         || this.n().S() != this && this.n().D() == this) {
         int m = Astolfo.INSTANCE.getSidebarGui().U();
         GuiMainMenu.P.drawString(this.H.getName(), this.M() + 172.5F - GuiMainMenu.P.getStringWidth(this.H.getName()) / 2.0F, sidebar.E_float() + 3.0F, m);
         RenderUtil.drawRect(this.M() + 135.0F, sidebar.E_float() + 17.0F, this.M() + 210.0F, sidebar.E_float() + 18.0F, m);
         if (this.A() != null) {
            GuiMainMenu.P
               .drawString(this.A().X().getName(), this.M() + 310.0F - GuiMainMenu.P.getStringWidth(this.A().X().getName()) / 2.0F, sidebar.E_float() + 3.0F, -1);
         }

         GL11.glEnable(3089);
         SidebarGui.c(sidebar.M(), sidebar.E_float() + 20.0F, sidebar.M() + sidebar.x(), sidebar.E_float() + sidebar.Y());

         for (Element element : this.M_ArrayList()) {
            if (element instanceof SidebarSettingComponent) {
               SidebarSettingComponent sidebarSettingComponent = (SidebarSettingComponent)element;
               sidebarSettingComponent.r(sidebar.M() + 135.0F, sidebar.E_float() + l);
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

      this.f = this.n().t().x() == 115;
   }

   @Override
   public void W(int i, int j, int k) {
      CategoryTab categoryTab = this.n();
      int l = categoryTab.O + k;
      int m = categoryTab.t().Y() - categoryTab.g();
      if (l > 12) {
         categoryTab.O = 12;
      } else if (l >= m) {
         categoryTab.O += k;
      }
   }

   @Override
   public void P(char c, int i) {
      if (this.Q) {
         if (i == 1) {
            this.Q = false;
            return;
         }

         toasts.B("Bind", String.format("%s has been binded to %s", this.H.getName(), Keyboard.getKeyName(i)));
         this.H.setKey(i);
         this.Q = false;
      }

      super.P(c, i);
   }

   public int v() {
      byte b = 5;

      for (Element element : this.M_ArrayList()) {
         if (element instanceof SidebarSettingComponent) {
            b += 15;
         }
      }

      return b;
   }

   public int F(SidebarSettingComponent sidebarSettingComponent) {
      byte b = 5;

      for (Element element : this.M_ArrayList()) {
         if (element instanceof SidebarSettingComponent) {
            if (sidebarSettingComponent == element) {
               return b;
            }

            b += 15;
         }
      }

      return b;
   }

   public CategoryTab n() {
      return (CategoryTab)this.O();
   }

   public void D(MapComponent mapComponent) {
      MapComponent mapComponent2 = this.d;
      this.d = mapComponent;
      this.C = mapComponent2;
   }

   public MapComponent A() {
      return this.d;
   }

   public MapComponent h() {
      return this.C;
   }

   public boolean O_boolean() {
      return this.Q;
   }
}
