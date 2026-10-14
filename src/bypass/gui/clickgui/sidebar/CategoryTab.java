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

   public CategoryTab(Sidebar sidebar, Category category, int i, int j, int k, int l) {
      super(category.getDisplayName(), i, j, k, l);
      this.f(sidebar);
      this.H = category;
      this.f = this.g();
   }

   @Override
   public void l(int i) {
      if (i == 0) {
         if (this.t().z() == this) {
            this.n = null;
            this.t().W(null);
         } else {
            this.t().W(this);
         }
      }
   }

   @Override
   public void render(int i, int j) {
      long l = d ^ 98381257715873L;
      long m = l ^ 8972929756414L;
      if (!this.Q.isRunning()) {
         if (!this.G(i, j) && this.t().z() != this) {
            this.Q.backwards().start();
         } else {
            this.Q.forwards().start();
         }
      }

      this.u = (int)this.Q.getValue(0.0, 255.0);
      int k = Math.min(this.u, client.getSidebarGui().e);
      int n = client.getSidebarGui().F();
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), n);
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), new Color(29, 31, 37, k).getRGB());
      RenderUtil.setColor(new Color(136, 141, 157, client.getSidebarGui().e).getRGB());
      Minecraft.getMinecraft()
         .getTextureManager()
         .bindTexture(new ResourceLocation(String.format("client/categories/%s_good.png", this.H.getDisplayName().toLowerCase())));
      Gui.drawModalRectWithCustomSizedTexture((int)(this.M() + 10.0F), (int)this.E_float() + 6, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
      RenderUtil.setColor(new Color(255, 255, 255, k).getRGB());
      Minecraft.getMinecraft()
         .getTextureManager()
         .bindTexture(new ResourceLocation(String.format("client/categories/%s_good.png", this.H.getDisplayName().toLowerCase())));
      Gui.drawModalRectWithCustomSizedTexture((int)(this.M() + 10.0F), (int)this.E_float() + 6, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
      if (this.t().z() == this || k > 35) {
         GuiMainMenu.c
            .drawString(
               this.J().toUpperCase(),
               this.M() + this.x() / 2.0F - GuiMainMenu.c.getStringWidth(this.J()) / 2.0F - 0.5F,
               this.E_float() + 23.0F,
               new Color(255, 255, 255, k).getRGB()
            );
      }

      int i2 = this.O;
      if (!this.e && this.t().s() == this && this.t().z() == null
         || this.t().s() == null && this.t().z() == this
         || this.t().s() != this && this.t().z() == this) {
         GL11.glEnable(3089);
         SidebarGui.c(this.t().M(), this.t().E_float(), this.t().M() + this.t().x(), this.t().E_float() + this.t().Y());

         for (Element element : this.M_ArrayList()) {
            if (element instanceof ModuleButton) {
               ModuleButton moduleButton = (ModuleButton)element;
               moduleButton.r(this.t().M() + 40.0F, this.t().E_float() + i2);
               moduleButton.render(i, j);
               if (!moduleButton.j.isRunning()) {
                  moduleButton.j.forwards().start();
               }

               moduleButton.P = (int)moduleButton.j.getValue(0.0, 255.0);
               i2 += 15;
            } else if (element instanceof Button) {
               Component component = (Component)element;
               component.r(this.t().M() + 40.0F, this.t().E_float() + i2);
               component.render(i, j);
               i2 += 13;
            }

            if (element instanceof StringComponent) {
               Component component2 = (Component)element;
               component2.r(this.t().M() + 40.0F, this.t().E_float() + i2);
               component2.render(i, j);
               i2 += 20;
            }
         }

         GL11.glDisable(3089);
      } else {
         for (Element element2 : this.M_ArrayList()) {
            if (element2 instanceof ModuleButton) {
               ModuleButton moduleButton2 = (ModuleButton)element2;
               if (!moduleButton2.j.isRunning()) {
                  moduleButton2.j.backwards().start();
               }

               moduleButton2.P = (int)moduleButton2.j.getValue(0.0, 255.0);
            }
         }
      }

      this.e = this.t().x() == 34;
   }

   public int g() {
      byte b = 5;

      for (Element element : this.M_ArrayList()) {
         if (element instanceof ModuleButton) {
            b += 15;
         }
      }

      return b;
   }

   public int x(ModuleButton moduleButton) {
      byte b = 5;

      for (Element element : this.M_ArrayList()) {
         if (element instanceof ModuleButton) {
            if (moduleButton == element) {
               return b;
            }

            b += 15;
         }
      }

      return b;
   }

   public void j(ModuleButton moduleButton) {
      if (moduleButton == null) {
         if (this.w != null) {
            this.w.D(null);
         }
      } else {
         moduleButton.D(null);
      }

      ModuleButton moduleButton2 = this.n;
      this.n = moduleButton;
      this.w = moduleButton2;
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
