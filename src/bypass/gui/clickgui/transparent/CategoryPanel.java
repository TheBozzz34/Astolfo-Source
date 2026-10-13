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

   public CategoryPanel(Category var1, int var2, int var3, int var4, int var5) {
      super(var1.getDisplayName(), var2, var3, var4, var5);
      this.u = var1;
      this.F(true);
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = d ^ 95156458949240L;
      long var5 = var3 ^ 39308001293406L;
      int var8 = this.Q() + 4;
      RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y() + 6.0F, client.getColor().getRGB());
      client.getCustomFontRenderer().drawStringWithShadow(this.u.getDisplayName(), this.M() + 2.0F, this.E_float() + 2.0F, Color.WHITE.getRGB());
      RenderUtil.setColor(this.s_boolean() ? client.getColor().brighter().getRGB() : new Color(55, 55, 55).getRGB());
      Minecraft.getMinecraft()
         .getTextureManager()
         .bindTexture(new ResourceLocation(String.format("client/categories/%s.png", this.u.getDisplayName().toLowerCase())));
      Gui.drawModalRectWithCustomSizedTexture((int)(this.M() + this.x() - 10.0F), (int)this.E_float() + 4, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      if (this.s_boolean() || this.U.isRunning()) {
         int var9 = 0;

         for (Element var11 : this.M_ArrayList()) {
            if (var11 instanceof ModuleButton) {
               ModuleButton var12 = (ModuleButton)var11;
               var12.r(this.M(), this.E_float() + var9 + 17.0F);
               if (this.s_boolean()) {
                  var12.render(var1, var2);
               }

               var9 += !var12.s_boolean() ? 15 : var12.d() + (this.h((ModuleButton)var11) ? this.N((ModuleButton)var11) : 0);
               var9 = (int)(var9 - this.U.getValue(0.0, var9));
            }
         }
      }
   }

   @Override
   public void l(int var1) {
      if (var1 == 1) {
         this.u(!this.s_boolean());
         if (this.s_boolean()) {
            this.U.backwards().rewind().start();
         } else {
            this.U.forwards().rewind().start();
         }
      }
   }

   private int Q() {
      int var4 = 0;
      if (this.s_boolean()) {
         for (Element var6 : this.M_ArrayList()) {
            if (var6 instanceof ModuleButton) {
               if (((ModuleButton)var6).s_boolean()) {
                  var4 += ((ModuleButton)var6).d() + (this.h((ModuleButton)var6) ? this.N((ModuleButton)var6) : 0);
               } else {
                  var4 += 15;
               }
            }
         }
      }

      return var4;
   }

   private boolean h(ModuleButton var1) {
      if (!var1.M_ArrayList().isEmpty()) {
         for (Element var6 : var1.M_ArrayList()) {
            if (var6 instanceof MapComponent && ((MapComponent)var6).s_boolean()) {
               return true;
            }
         }
      }

      return false;
   }

   private int N(ModuleButton var1) {
      int var5 = 0;
      if (!var1.M_ArrayList().isEmpty()) {
         for (Element var7 : var1.M_ArrayList()) {
            if (var7 instanceof MapComponent) {
               var5 += ((MapComponent)var7).d();
            }
         }
      }

      return var5;
   }

   public Category p() {
      return this.u;
   }
}
