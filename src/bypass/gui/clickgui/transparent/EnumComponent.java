package bypass.gui.clickgui.transparent;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.render.RenderUtil;
import bypass.setting.EnumSetting;
import bypass.setting.Setting;
import java.awt.Color;
import net.minecraft.util.EnumChatFormatting;

public class EnumComponent extends SettingComponent {
   public EnumComponent(Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
   }

   @Override
   public void render(int i, int j) {
      RenderUtil.drawRect(
         this.M() + 1.5F, this.E_float() - 1.0F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + 4.0F, new Color(1, 1, 1, 120).getRGB()
      );
      client.getCustomFontRenderer()
         .drawStringWithShadow(
            EnumChatFormatting.GRAY + this.j().getName().replace("-", " ") + ":" + EnumChatFormatting.WHITE + " " + this.j().getValue().name(),
            this.M() + 4.0F,
            this.E_float(),
            Color.WHITE.getRGB()
         );
   }

   @Override
   public void l(int i) {
      if (this.I().s_boolean()) {
         Enum[] enums = (Enum[])this.j().getValue().getClass().getEnumConstants();
         int k = this.w();
         k += i == 0 ? 1 : -1;
         if (k >= enums.length || k < 0) {
            k = 0;
         }

         this.j().setValue(enums[k]);
      }
   }

   private EnumSetting j() {
      return (EnumSetting)this.t();
   }

   private int w() {
      Enum[] enums = (Enum[])this.j().getValue().getClass().getEnumConstants();

      for (int i = 0; i < enums.length; i++) {
         if (enums[i] == this.j().getValue()) {
            return i;
         }
      }

      return -1;
   }
}
