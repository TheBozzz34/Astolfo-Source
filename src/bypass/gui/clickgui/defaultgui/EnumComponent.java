package bypass.gui.clickgui.defaultgui;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.setting.EnumSetting;
import bypass.setting.Setting;
import net.minecraft.client.gui.GuiMainMenu;

public class EnumComponent extends SettingComponent {
   public EnumComponent(Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
   }

   @Override
   public void render(int i, int j) {
      GuiMainMenu.latoBoldFont15.drawString(this.getEnumSetting().getName().replace("-", " "), this.getX() + 3.0F, this.getYPos(), -1);
      GuiMainMenu.latoBoldFont15
         .drawString(
            String.valueOf(this.getEnumSetting().getValue()),
            this.getX() + this.getWidth() - GuiMainMenu.latoBoldFont15.getStringWidth(String.valueOf(this.getEnumSetting().getValue()), true),
            this.getYPos(),
            13421772
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
