package bypass.gui.clickgui.defaultgui;

import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.module.Category;
import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;

public class CategoryPanel extends Container {
   private Category X;
   private static final ResourceLocation O = new ResourceLocation("client/icon/eye_open.png");
   private static final ResourceLocation H = new ResourceLocation("client/icon/eye_close.png");
   private static final long d = 100998427593824L;

   public CategoryPanel(Category var1, int var2, int var3, int var4, int var5) {
      super(var1.getDisplayName(), var2, var3, var4, var5);
      this.X = var1;
      this.F(true);
   }

   @Override
   public void l(int var1) {
      if (var1 == 1) {
         this.u(!this.s_boolean());
      }
   }

   @Override
   public void render(int var1, int var2) {
      int var7 = this.m() + 2;
      int var8 = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.X.getColor() : client.getColorSetting().getValue().getRGB();
      RenderUtil.drawRect(this.M() - 0.5F, this.E_float() - 0.5F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + var7 + 0.5F, var8);
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y() + var7, new Color(1710618).getRGB());
      RenderUtil.drawRect(
         this.M() + 2.0F, this.E_float() + 17.0F, this.M() + this.x() - 2.0F, this.E_float() + this.Y() + var7 - 2.0F, new Color(37, 37, 37).getRGB()
      );
      RenderUtil.setColor(this.s_boolean() ? var8 : new Color(1710618).brighter().brighter().getRGB());
      Minecraft.getMinecraft().getTextureManager().bindTexture(this.s_boolean() ? O : H);
      Gui.drawModalRectWithCustomSizedTexture((int)(this.M() + this.x() - 21.0F), (int)this.E_float() + 4, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      RenderUtil.setColor(var8);
      Minecraft.getMinecraft()
         .getTextureManager()
         .bindTexture(new ResourceLocation(String.format("client/categories/%s.png", this.X.getDisplayName().toLowerCase())));
      Gui.drawModalRectWithCustomSizedTexture((int)(this.M() + this.x() - 11.0F), (int)this.E_float() + 4, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GuiMainMenu.m.drawStringWithShadow(this.X.getDisplayName().toLowerCase(), this.M() + 4.0F, this.E_float() + 3.5F, -1);
      if (this.s_boolean() || this.U.isRunning()) {
         byte var9 = 0;

         for (Element var11 : this.M_ArrayList()) {
            if (var11 instanceof ModuleButton) {
               ModuleButton var12 = (ModuleButton)var11;
               var12.r(this.M() + 2.0F, this.E_float() + var9 + 17.0F);
               if (this.s_boolean()) {
                  var12.render(var1, var2);
               }

               var9 += !var12.s_boolean() ? 15 : var12.c() + (this.v((ModuleButton)var11) ? this.m((ModuleButton)var11) + 2 : 0);
            }
         }
      }
   }

   private int m() {
      int var4 = 0;
      if (this.s_boolean()) {
         for (Element var6 : this.M_ArrayList()) {
            if (var6 instanceof ModuleButton) {
               if (((ModuleButton)var6).s_boolean()) {
                  var4 += ((ModuleButton)var6).c() + (this.v((ModuleButton)var6) ? this.m((ModuleButton)var6) + 2 : 0);
               } else {
                  var4 += 15;
               }
            }
         }
      }

      return var4;
   }

   private boolean v(ModuleButton var1) {
      if (!var1.M_ArrayList().isEmpty()) {
         for (Element var6 : var1.M_ArrayList()) {
            if (var6 instanceof MapComponent && ((MapComponent)var6).s_boolean()) {
               return true;
            }
         }
      }

      return false;
   }

   private int m(ModuleButton var1) {
      int var5 = 0;
      if (!var1.M_ArrayList().isEmpty()) {
         for (Element var7 : var1.M_ArrayList()) {
            if (var7 instanceof MapComponent) {
               var5 += ((MapComponent)var7).i();
            }
         }
      }

      return var5;
   }

   public Category c() {
      return this.X;
   }
}
