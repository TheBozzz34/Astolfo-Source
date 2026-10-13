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
   private static final long c = 130516420392357L;

   public BooleanComponent(Category var1, Container var2, Setting var3, int var4, int var5) {
      super(var2, var3, var4, var5);
      this.j = var1;
      this.V = var2;
   }

   @Override
   public void l(int var1) {
      if (this.V.s_boolean() && var1 == 0) {
         this.d().set(!this.d().getValue());
      }
   }

   @Override
   public void render(int var1, int var2) {
      int var6 = HUD.gui.getSetting("Category-Colors").asBoolean() ? this.j.getColor() : client.getColorSetting().getValue().getRGB();
      if (this.d().getValue()) {
         RenderUtil.drawRect(this.M(), this.E_float(), this.M() + this.x(), this.E_float() + this.Y(), var6);
      }

      GuiMainMenu.m.drawString(this.d().getName(), this.M() + 3.0F, this.E_float(), -1);
   }

   private BooleanSetting d() {
      return (BooleanSetting)this.t();
   }
}
