package bypass.gui.clickgui.sidebar;

import bypass.gui.animation.Animation;
import bypass.gui.animation.Easing;
import bypass.gui.animation.TimedAnimation;
import bypass.gui.component.Button;
import bypass.gui.component.Component;
import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.module.Category;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CategoryTab extends Container {
   public Animation Q = new TimedAnimation().setDuration(200).setEasing(Easing.LINEAR);
   private ModuleButton n;
   private ModuleButton w;
   private Category H;
   public boolean e;
   public int O = 12;
   public final int f;
   private int u;
   private static final long d = 86268410998483L;

   public CategoryTab(Sidebar var1, Category var2, int var3, int var4, int var5, int var6) {
      super(var2.getDisplayName(), var3, var4, var5, var6);
      this.f(var1);
      this.H = var2;
      this.f = this.g();
   }

   @Override
   public void l(int var1) {
      if (var1 == 0) {
         if (this.t().z() == this) {
            this.n = null;
            this.t().W(null);
         } else {
            this.t().W(this);
         }
      }
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = d ^ 98381257715873L;
      long var5 = var3 ^ 8972929756414L;
      if (!this.Q.isRunning()) {
         if (!this.G(var1, var2) && this.t().z() != this) {
            this.Q.backwards().start();
         } else {
            this.Q.forwards().start();
         }
      }

      this.u = (int)this.Q.getValue(0.0, 255.0);
      int var9 = Math.min(this.u, client.getSidebarGui().e);
      int var10 = client.getSidebarGui().F();
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), var10);
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(29, 31, 37, var9).getRGB());
      RenderUtil.setColor(new Color(136, 141, 157, client.getSidebarGui().e).getRGB());
      Minecraft.getMinecraft()
         .getTextureManager()
         .bindTexture(new ResourceLocation(String.format("client/categories/%s_good.png", this.H.getDisplayName().toLowerCase())));
      Gui.drawModalRectWithCustomSizedTexture((int)(this.M() + 10.0F), (int)this.E_float() + 6, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
      RenderUtil.setColor(new Color(255, 255, 255, var9).getRGB());
      Minecraft.getMinecraft()
         .getTextureManager()
         .bindTexture(new ResourceLocation(String.format("client/categories/%s_good.png", this.H.getDisplayName().toLowerCase())));
      Gui.drawModalRectWithCustomSizedTexture((int)(this.M() + 10.0F), (int)this.E_float() + 6, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
      if (this.t().z() == this || var9 > 35) {
         GuiMainMenu.c
            .drawString(
               this.J().toUpperCase(),
               this.M() + this.x() / 2.0F - GuiMainMenu.c.getStringWidth(this.J()) / 2.0F - 0.5F,
               this.E_float() + 23.0F,
               new Color(255, 255, 255, var9).getRGB()
            );
      }

      int var11 = this.O;
      if (!this.e && this.t().s() == this && this.t().z() == null
         || this.t().s() == null && this.t().z() == this
         || this.t().s() != this && this.t().z() == this) {
         GL11.glEnable(3089);
         SidebarGui.c(this.t().M(), this.t().E_float(), this.t().M() + this.t().x(), this.t().E_float() + this.t().Y());

         for (Element var16 : this.M_ArrayList()) {
            if (var16 instanceof ModuleButton) {
               ModuleButton var17 = (ModuleButton)var16;
               var17.r(this.t().M() + 40.0F, this.t().E_float() + var11);
               var17.render(var1, var2);
               if (!var17.j.isRunning()) {
                  var17.j.forwards().start();
               }

               var17.P = (int)var17.j.getValue(0.0, 255.0);
               var11 += 15;
            } else if (var16 instanceof Button) {
               Component var18 = (Component)var16;
               var18.r(this.t().M() + 40.0F, this.t().E_float() + var11);
               var18.render(var1, var2);
               var11 += 13;
            }

            if (var16 instanceof StringComponent) {
               Component var19 = (Component)var16;
               var19.r(this.t().M() + 40.0F, this.t().E_float() + var11);
               var19.render(var1, var2);
               var11 += 20;
            }
         }

         GL11.glDisable(3089);
      } else {
         for (Element var13 : this.M_ArrayList()) {
            if (var13 instanceof ModuleButton) {
               ModuleButton var14 = (ModuleButton)var13;
               if (!var14.j.isRunning()) {
                  var14.j.backwards().start();
               }

               var14.P = (int)var14.j.getValue(0.0, 255.0);
            }
         }
      }

      this.e = this.t().x() == 34;
   }

   public int g() {
      byte var4 = 5;

      for (Element var6 : this.M_ArrayList()) {
         if (var6 instanceof ModuleButton) {
            var4 += 15;
         }
      }

      return var4;
   }

   public int x(ModuleButton var1) {
      byte var5 = 5;

      for (Element var7 : this.M_ArrayList()) {
         if (var7 instanceof ModuleButton) {
            if (var1 == var7) {
               return var5;
            }

            var5 += 15;
         }
      }

      return var5;
   }

   public void j(ModuleButton var1) {
      if (var1 == null) {
         if (this.w != null) {
            this.w.D(null);
         }
      } else {
         var1.D(null);
      }

      ModuleButton var5 = this.n;
      this.n = var1;
      this.w = var5;
   }

   public ModuleButton D() {
      return this.n;
   }

   public ModuleButton S() {
      return this.w;
   }

   public Sidebar t() {
      return (Sidebar)this.O();
   }
}
