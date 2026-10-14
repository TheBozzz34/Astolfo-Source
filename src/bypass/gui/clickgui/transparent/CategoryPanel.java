package bypass.gui.clickgui.transparent;

import bypass.gui.component.Container;
import bypass.gui.component.Element;
import bypass.module.Category;
import bypass.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class CategoryPanel extends Container {
   private Category u;
   private static final Color C = new Color(38, 37, 38);
   private static final Color W = new Color(26, 26, 26);
   private static final long d = 72037623796746L;

   public CategoryPanel(Category category, int i, int j, int k, int l) {
      super(category.getDisplayName(), i, j, k, l);
      this.u = category;
      this.F(true);
   }

   @Override
   public void render(int i, int j) {
      long l = d ^ 95156458949240L;
      long m = l ^ 39308001293406L;
      int k = this.Q() + 4;
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y() + 6.0F, client.getColor().getRGB());
      client.getCustomFontRenderer().drawStringWithShadow(this.u.getDisplayName(), this.M() + 2.0F, this.E_float() + 2.0F, Color.WHITE.getRGB());
      RenderUtil.setColor(this.s_boolean() ? client.getColor().brighter().getRGB() : new Color(55, 55, 55).getRGB());
      Minecraft.getMinecraft()
         .getTextureManager()
         .bindTexture(new ResourceLocation(String.format("client/categories/%s.png", this.u.getDisplayName().toLowerCase())));
      Gui.drawModalRectWithCustomSizedTexture((int)(this.M() + this.x() - 10.0F), (int)this.E_float() + 4, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      if (this.s_boolean() || this.U.isRunning()) {
         int n = 0;

         for (Element element : this.M_ArrayList()) {
            if (element instanceof ModuleButton) {
               ModuleButton moduleButton = (ModuleButton)element;
               moduleButton.r(this.M(), this.E_float() + n + 17.0F);
               if (this.s_boolean()) {
                  moduleButton.render(i, j);
               }

               n += !moduleButton.s_boolean() ? 15 : moduleButton.d() + (this.h((ModuleButton)element) ? this.N((ModuleButton)element) : 0);
               n = (int)(n - this.U.getValue(0.0, n));
            }
         }
      }
   }

   @Override
   public void l(int i) {
      if (i == 1) {
         this.u(!this.s_boolean());
         if (this.s_boolean()) {
            this.U.backwards().rewind().start();
         } else {
            this.U.forwards().rewind().start();
         }
      }
   }

   private int Q() {
      int i = 0;
      if (this.s_boolean()) {
         for (Element element : this.M_ArrayList()) {
            if (element instanceof ModuleButton) {
               if (((ModuleButton)element).s_boolean()) {
                  i += ((ModuleButton)element).d() + (this.h((ModuleButton)element) ? this.N((ModuleButton)element) : 0);
               } else {
                  i += 15;
               }
            }
         }
      }

      return i;
   }

   private boolean h(ModuleButton moduleButton) {
      if (!moduleButton.M_ArrayList().isEmpty()) {
         for (Element element : moduleButton.M_ArrayList()) {
            if (element instanceof MapComponent && ((MapComponent)element).s_boolean()) {
               return true;
            }
         }
      }

      return false;
   }

   private int N(ModuleButton moduleButton) {
      int i = 0;
      if (!moduleButton.M_ArrayList().isEmpty()) {
         for (Element element : moduleButton.M_ArrayList()) {
            if (element instanceof MapComponent) {
               i += ((MapComponent)element).d();
            }
         }
      }

      return i;
   }

   public Category p() {
      return this.u;
   }
}
