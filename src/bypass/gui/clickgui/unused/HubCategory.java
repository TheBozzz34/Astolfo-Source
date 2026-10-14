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

   public HubCategory(HubPanel hubPanel, Category category, int i, int j, int k, int l) {
      super(category.getDisplayName(), i, j, k, l);
      this.B = hubPanel;
      this.j = category;
   }

   @Override
   public void render(int i, int j) {
      long l = d ^ 119906979796918L;
      long m = l ^ 35699865445313L;
      if (this.B.n() != this) {
         if (this.G(i, j)) {
            RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), client.getColorSetting().getValue().getRGB());
            RenderUtil.drawRect(
               this.M() + 0.5F, this.E_float() + 0.5F, this.M() + this.x() - 0.5F, this.E_float() + this.Y() - 0.5F, new Color(32, 31, 32).getRGB()
            );
         }

         RenderUtil.setColor(this.G(i, j) ? client.getColorSetting().getValue().getRGB() : new Color(143, 142, 143).getRGB());
         client.getCustomFontRenderer()
            .drawString(this.J(), this.M() + this.x() / 2 - client.getCustomFontRenderer().getStringWidth(this.J()) / 2, this.E_float() + this.Y() - 18.0F, -1);
      } else {
         this.M_ArrayList().clear();

         for (ModuleInfo moduleInfo : client.getModuleManager().getModules(this.j)) {
            if (moduleInfo instanceof Module) {
               Module module = (Module)moduleInfo;
            }
         }
      }
   }

   @Override
   public void l(int i) {
   }

   public HubPanel K() {
      return this.B;
   }

   public void Z(HubPanel hubPanel) {
      this.B = hubPanel;
   }

   public Category X() {
      return this.j;
   }
}
