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
   private static final long e = 110176195880408L;

   public ModuleButton(CategoryTab var1, Module var2, int var3, int var4, int var5, int var6) {
      super(var2.getName(), var3, var4, var5, var6);
      this.f(var1);
      this.H = var2;
   }

   @Override
   public void l(int var1) {
      CategoryTab var5 = this.n();
      int var6 = var5.O;
      int var7 = var5.t().Y() - var5.x(this);
      boolean var8 = var7 >= var6 - 10 && var5.O().E_float() - 10.0F < this.E_float();
      if (var8) {
         switch (var1) {
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
   public void render(int var1, int var2) {
      long var3 = e ^ 126699976113066L;
      long var5 = var3 ^ 8972929756414L;
      if (!this.Y.isRunning()) {
         if (this.G(var1, var2)) {
            this.Y.forwards().start();
         } else {
            this.Y.backwards().start();
         }
      }

      int var8 = (int)this.Y.getValue(0.0, 255.0);
      if (this.H.isEnabled() && this.P > 40) {
         RenderUtil.drawRect(this.M() - 6.0F, this.E_float() - 2.0F, this.M() - 5.0F, this.E_float() + 9.0F, new Color(255, 255, 255, this.P).getRGB());
      }

      if (this.P > 40) {
         client.getCustomFontRenderer().drawString(this.H.getName(), this.M(), this.E_float() - 2.0F, new Color(136, 141, 157, this.P).getRGB());
      }

      if (var8 > 40) {
         client.getCustomFontRenderer().drawString(this.H.getName(), this.M(), this.E_float() - 2.0F, new Color(255, 255, 255, var8).getRGB());
      }

      int var9 = this.A;
      Sidebar var10 = this.n().t();
      if (!this.f && this.n().S() == this && this.n().D() == null
         || this.n().S() == null && this.n().D() == this
         || this.n().S() != this && this.n().D() == this) {
         int var15 = Astolfo.INSTANCE.getSidebarGui().U();
         GuiMainMenu.P.drawString(this.H.getName(), this.M() + 172.5F - GuiMainMenu.P.getStringWidth(this.H.getName()) / 2.0F, var10.E_float() + 3.0F, var15);
         RenderUtil.drawRect(this.M() + 135.0F, var10.E_float() + 17.0F, this.M() + 210.0F, var10.E_float() + 18.0F, var15);
         if (this.A() != null) {
            GuiMainMenu.P
               .drawString(this.A().X().getName(), this.M() + 310.0F - GuiMainMenu.P.getStringWidth(this.A().X().getName()) / 2.0F, var10.E_float() + 3.0F, -1);
         }

         GL11.glEnable(3089);
         SidebarGui.c(var10.M(), var10.E_float() + 20.0F, var10.M() + var10.x(), var10.E_float() + var10.Y());

         for (Element var17 : this.M_ArrayList()) {
            if (var17 instanceof SidebarSettingComponent) {
               SidebarSettingComponent var14 = (SidebarSettingComponent)var17;
               var14.r(var10.M() + 135.0F, var10.E_float() + var9);
               var14.render(var1, var2);
               if (!var14.B.isRunning()) {
                  var14.B.forwards().start();
               }

               var14.X = (int)var14.B.getValue(0.0, 255.0);
               var9 += 15;
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

      this.f = this.n().t().x() == 115;
   }

   @Override
   public void W(int var1, int var2, int var3) {
      CategoryTab var7 = this.n();
      int var8 = var7.O + var3;
      int var9 = var7.t().Y() - var7.g();
      if (var8 > 12) {
         var7.O = 12;
      } else if (var8 >= var9) {
         var7.O += var3;
      }
   }

   @Override
   public void P(char var1, int var2) {
      if (this.Q) {
         if (var2 == 1) {
            this.Q = false;
            return;
         }

         toasts.B("Bind", String.format("%s has been binded to %s", this.H.getName(), Keyboard.getKeyName(var2)));
         this.H.setKey(var2);
         this.Q = false;
      }

      super.P(var1, var2);
   }

   public int v() {
      byte var4 = 5;

      for (Element var6 : this.M_ArrayList()) {
         if (var6 instanceof SidebarSettingComponent) {
            var4 += 15;
         }
      }

      return var4;
   }

   public int F(SidebarSettingComponent var1) {
      byte var5 = 5;

      for (Element var7 : this.M_ArrayList()) {
         if (var7 instanceof SidebarSettingComponent) {
            if (var1 == var7) {
               return var5;
            }

            var5 += 15;
         }
      }

      return var5;
   }

   public CategoryTab n() {
      return (CategoryTab)this.O();
   }

   public void D(MapComponent var1) {
      MapComponent var2 = this.d;
      this.d = var1;
      this.C = var2;
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
