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
         this.getX() + 1.5F,
         this.getYPos() - 1.0F,
         this.getX() + this.getWidth() + 0.5F,
         this.getYPos() + this.getHeight() + 4.0F,
         new Color(1, 1, 1, 120).getRGB()
      );
      client.getCustomFontRenderer()
         .drawStringWithShadow(
            EnumChatFormatting.GRAY
               + this.getEnumSetting().getName().replace("-", " ")
               + ":"
               + EnumChatFormatting.WHITE
               + " "
               + this.getEnumSetting().getValue().name(),
            this.getX() + 4.0F,
            this.getYPos(),
            Color.WHITE.getRGB()
         );
   }

   @Override
   public void onClick(int i) {
      if (this.getParentContainer().isExpanded()) {
         Enum[] enums = (Enum[])this.getEnumSetting().getValue().getClass().getEnumConstants();
         int j = this.getValueIndex();
         j += i == 0 ? 1 : -1;
         if (j >= enums.length || j < 0) {
            j = 0;
         }

         this.getEnumSetting().setValue(enums[j]);
      }
   }

   private EnumSetting getEnumSetting() {
      return (EnumSetting)this.getSetting();
   }

   private int getValueIndex() {
      Enum[] enums = (Enum[])this.getEnumSetting().getValue().getClass().getEnumConstants();

      for (int i = 0; i < enums.length; i++) {
         if (enums[i] == this.getEnumSetting().getValue()) {
            return i;
         }
      }

      return -1;
   }
}
