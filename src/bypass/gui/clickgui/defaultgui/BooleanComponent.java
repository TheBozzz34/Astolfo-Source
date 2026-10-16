package bypass.gui.clickgui.defaultgui;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.module.Category;
import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.Setting;
import net.minecraft.client.gui.GuiMainMenu;

public class BooleanComponent extends SettingComponent {
   private final Category category;
   private final Container ownerContainer;

   public BooleanComponent(Category category, Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
      this.category = category;
      this.ownerContainer = container;
   }

   @Override
   public void onClick(int i) {
      if (this.ownerContainer.isExpanded() && i == 0) {
         this.getBooleanSetting().set(!this.getBooleanSetting().getValue());
      }
   }

   @Override
   public void render(int i, int j) {
      int k = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.category.getColor() : client.getColorSetting().getValue().getRGB();
      if (this.getBooleanSetting().getValue()) {
         RenderUtil.drawRect(this.getX(), this.getYPos(), this.getX() + this.getWidth(), this.getYPos() + this.getHeight(), k);
      }

      GuiMainMenu.m.drawString(this.getBooleanSetting().getName(), this.getX() + 3.0F, this.getYPos(), -1);
   }

   private BooleanSetting getBooleanSetting() {
      return (BooleanSetting)this.getSetting();
   }
}
