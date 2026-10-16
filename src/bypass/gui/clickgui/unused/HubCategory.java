package bypass.gui.clickgui.unused;

import bypass.gui.component.Container;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.ModuleInfo;
import bypass.render.RenderUtil;
import java.awt.Color;

public class HubCategory extends Container {
   private HubPanel hubPanel;
   private Category category;

   public HubCategory(HubPanel hubPanel, Category category, int i, int j, int k, int l) {
      super(category.getDisplayName(), i, j, k, l);
      this.hubPanel = hubPanel;
      this.category = category;
   }

   @Override
   public void render(int i, int j) {
      if (this.hubPanel.getSelectedCategory() != this) {
         if (this.isHovered(i, j)) {
            RenderUtil.drawRect(
               this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), client.getColorSetting().getValue().getRGB()
            );
            RenderUtil.drawRect(
               this.getX() + 0.5F,
               this.getYPos() + 0.5F,
               this.getX() + this.getWidth() - 0.5F,
               this.getYPos() + this.getHeight() - 0.5F,
               new Color(32, 31, 32).getRGB()
            );
         }

         RenderUtil.setColor(this.isHovered(i, j) ? client.getColorSetting().getValue().getRGB() : new Color(143, 142, 143).getRGB());
         client.getCustomFontRenderer()
            .drawString(
               this.getName(),
               this.getX() + this.getWidth() / 2 - client.getCustomFontRenderer().getStringWidth(this.getName()) / 2,
               this.getYPos() + this.getHeight() - 18.0F,
               -1
            );
      } else {
         this.getChildren().clear();

         for (ModuleInfo moduleInfo : client.getModuleManager().getModules(this.category)) {
            if (moduleInfo instanceof Module) {
               Module module = (Module)moduleInfo;
            }
         }
      }
   }

   @Override
   public void onClick(int i) {
   }

   public HubPanel getHubPanel() {
      return this.hubPanel;
   }

   public void setHubPanel(HubPanel hubPanel) {
      this.hubPanel = hubPanel;
   }

   public Category getCategory() {
      return this.category;
   }
}
