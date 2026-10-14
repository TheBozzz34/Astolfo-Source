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
   private final Category j;
   private final Container V;

   public BooleanComponent(Category category, Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
      this.j = category;
      this.V = container;
   }

   @Override
   public void l(int i) {
      if (this.V.s_boolean() && i == 0) {
         this.d().set(!this.d().getValue());
      }
   }

   @Override
   public void render(int i, int j) {
      int k = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.j.getColor() : client.getColorSetting().getValue().getRGB();
      if (this.d().getValue()) {
         RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), k);
      }

      GuiMainMenu.m.drawString(this.d().getName(), this.M() + 3.0F, this.E_float(), -1);
   }

   private BooleanSetting d() {
      return (BooleanSetting)this.t();
   }
}
