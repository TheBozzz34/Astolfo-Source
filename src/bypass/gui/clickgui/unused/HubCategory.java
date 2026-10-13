package bypass.gui.clickgui.unused;

import bypass.gui.component.Container;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.ModuleInfo;
import bypass.render.RenderUtil;
import java.awt.Color;

public class HubCategory extends Container {
   private HubPanel B;
   private Category j;
   private static final long d = 134147984771524L;

   public HubCategory(HubPanel var1, Category var2, int var3, int var4, int var5, int var6) {
      super(var2.getDisplayName(), var3, var4, var5, var6);
      this.B = var1;
      this.j = var2;
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = d ^ 119906979796918L;
      long var5 = var3 ^ 35699865445313L;
      if (this.B.n() != this) {
         if (this.G(var1, var2)) {
            RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), client.getColorSetting().getValue().getRGB());
            RenderUtil.drawRect(
               this.M() + 0.5F, this.E_float() + 0.5F, this.M() + this.x() - 0.5F, this.E_float() + this.Y() - 0.5F, new Color(32, 31, 32).getRGB()
            );
         }

         RenderUtil.setColor(this.G(var1, var2) ? client.getColorSetting().getValue().getRGB() : new Color(143, 142, 143).getRGB());
         client.getCustomFontRenderer()
            .drawString(this.J(), this.M() + this.x() / 2 - client.getCustomFontRenderer().getStringWidth(this.J()) / 2, this.E_float() + this.Y() - 18.0F, -1);
      } else {
         this.M_ArrayList().clear();

         for (ModuleInfo var9 : client.getModuleManager().getModules(this.j)) {
            if (var9 instanceof Module) {
               Module var10 = (Module)var9;
            }
         }
      }
   }

   @Override
   public void l(int var1) {
   }

   public HubPanel K() {
      return this.B;
   }

   public void Z(HubPanel var1) {
      this.B = var1;
   }

   public Category X() {
      return this.j;
   }
}
