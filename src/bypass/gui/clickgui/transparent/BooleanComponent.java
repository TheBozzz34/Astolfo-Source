package bypass.gui.clickgui.transparent;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import bypass.setting.BooleanSetting;
import bypass.setting.Setting;
import java.awt.Color;
import net.minecraft.util.EnumChatFormatting;

public class BooleanComponent extends SettingComponent {
   private static final Color R = new Color(57, 57, 57);
   public static final Color W = new Color(22, 22, 22);
   private int Z;
   private static final long c = 12938358802752L;

   public BooleanComponent(Container var1, Setting var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = c ^ 31661229266738L;
      long var5 = var3 ^ 39308001293406L;
      if (this.G(var1, var2)) {
         if (this.Z < 120) {
            this.Z += 5;
         }
      } else if (this.Z > 0) {
         this.Z -= 5;
      }

      if (this.Z > 0) {
         RenderUtil.drawRect(
            this.M() + 1.5F, this.E_float() - 1.0F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + 4.0F, new Color(255, 255, 255, this.Z).getRGB()
         );
      }

      RenderUtil.drawRect(
         this.M() + 1.5F, this.E_float() - 1.0F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + 4.0F, new Color(1, 1, 1, 120).getRGB()
      );
      RenderUtil.drawRectOutline(this.M() + 3.0F, this.E_float() + 2.0F, this.M() + 12.0F, this.E_float() + 11.0F, new Color(137, 137, 137).getRGB());
      if (this.y().getValue()) {
         RenderUtil.drawLine(2.0F, this.M() + 6.0F, this.E_float() + 7.0F, this.M() + 8.0F, this.E_float() + 10.0F, client.getColor().getRGB());
         RenderUtil.drawLine(2.0F, this.M() + 11.0F, this.E_float() + 3.0F, this.M() + 8.0F, this.E_float() + 10.0F, client.getColor().getRGB());
      }

      client.getCustomFontRenderer()
         .drawStringWithShadow(EnumChatFormatting.GRAY + this.y().getName().replace("-", " "), this.M() + 15.0F, this.E_float() + 0.5F, Color.WHITE.getRGB());
      if (this.G(var1, var2) && HUD.toolTips.getValue()) {
         client.getCustomFontRenderer().drawStringWithShadow(this.y().getDescription(), var1 + 6, var2 - 4, -1);
      }
   }

   @Override
   public void l(int var1) {
      if (this.I().s_boolean() && var1 == 0) {
         this.y().set(!this.y().getValue());
      }
   }

   private BooleanSetting y() {
      return (BooleanSetting)this.t();
   }
}
