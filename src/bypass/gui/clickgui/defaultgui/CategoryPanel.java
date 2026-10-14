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

   public CategoryPanel(Category category, int i, int j, int k, int l) {
      super(category.getDisplayName(), i, j, k, l);
      this.X = category;
      this.F(true);
   }

   @Override
   public void l(int i) {
      if (i == 1) {
         this.u(!this.s_boolean());
      }
   }

   @Override
   public void render(int i, int j) {
      int k = this.m() + 2;
      int l = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.X.getColor() : client.getColorSetting().getValue().getRGB();
      RenderUtil.drawRect(this.M() - 0.5F, this.E_float() - 0.5F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + k + 0.5F, l);
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y() + k, new Color(1710618).getRGB());
      RenderUtil.drawRect(
         this.M() + 2.0F, this.E_float() + 17.0F, this.M() + this.x() - 2.0F, this.E_float() + this.Y() + k - 2.0F, new Color(37, 37, 37).getRGB()
      );
      RenderUtil.setColor(this.s_boolean() ? l : new Color(1710618).brighter().brighter().getRGB());
      Minecraft.getMinecraft().getTextureManager().bindTexture(this.s_boolean() ? O : H);
      Gui.drawModalRectWithCustomSizedTexture((int)(this.M() + this.x() - 21.0F), (int)this.E_float() + 4, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      RenderUtil.setColor(l);
      Minecraft.getMinecraft()
         .getTextureManager()
         .bindTexture(new ResourceLocation(String.format("client/categories/%s.png", this.X.getDisplayName().toLowerCase())));
      Gui.drawModalRectWithCustomSizedTexture((int)(this.M() + this.x() - 11.0F), (int)this.E_float() + 4, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      GuiMainMenu.m.drawStringWithShadow(this.X.getDisplayName().toLowerCase(), this.M() + 4.0F, this.E_float() + 3.5F, -1);
      if (this.s_boolean() || this.U.isRunning()) {
         byte b = 0;

         for (Element element : this.M_ArrayList()) {
            if (element instanceof ModuleButton) {
               ModuleButton moduleButton = (ModuleButton)element;
               moduleButton.r(this.M() + 2.0F, this.E_float() + b + 17.0F);
               if (this.s_boolean()) {
                  moduleButton.render(i, j);
               }

               b += !moduleButton.s_boolean() ? 15 : moduleButton.c() + (this.v((ModuleButton)element) ? this.m((ModuleButton)element) + 2 : 0);
            }
         }
      }
   }

   private int m() {
      int i = 0;
      if (this.s_boolean()) {
         for (Element element : this.M_ArrayList()) {
            if (element instanceof ModuleButton) {
               if (((ModuleButton)element).s_boolean()) {
                  i += ((ModuleButton)element).c() + (this.v((ModuleButton)element) ? this.m((ModuleButton)element) + 2 : 0);
               } else {
                  i += 15;
               }
            }
         }
      }

      return i;
   }

   private boolean v(ModuleButton moduleButton) {
      if (!moduleButton.M_ArrayList().isEmpty()) {
         for (Element element : moduleButton.M_ArrayList()) {
            if (element instanceof MapComponent && ((MapComponent)element).s_boolean()) {
               return true;
            }
         }
      }

      return false;
   }

   private int m(ModuleButton moduleButton) {
      int j = 0;
      if (!moduleButton.M_ArrayList().isEmpty()) {
         for (Element element : moduleButton.M_ArrayList()) {
            if (element instanceof MapComponent) {
               j += ((MapComponent)element).i();
            }
         }
      }

      return j;
   }

   public Category c() {
      return this.X;
   }
}
