package bypass.gui.clickgui.defaultgui;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.setting.EnumSetting;
import bypass.setting.Setting;
import net.minecraft.client.gui.GuiMainMenu;

public class EnumComponent extends SettingComponent {
   private static final long c = 5155583939849L;
   private static final long d = -7993921108442297140L;

   public EnumComponent(Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
   }

   @Override
   public void render(int i, int j) {
      GuiMainMenu.m.drawString(this.U().getName().replace("-", " "), this.M() + 3.0F, this.E_float(), -1);
      GuiMainMenu.m
         .drawString(
            String.valueOf(this.U().getValue()),
            this.M() + this.x() - GuiMainMenu.m.getStringWidth(String.valueOf(this.U().getValue()), true),
            this.E_float(),
            (int)d
         );
   }

   @Override
   public void l(int i) {
      if (this.I().s_boolean()) {
         Enum[] enums = (Enum[])this.U().getValue().getClass().getEnumConstants();
         int j = this.W();
         j += i == 0 ? 1 : -1;
         if (j >= enums.length || j < 0) {
            j = 0;
         }

         this.U().setValue(enums[j]);
      }
   }

   private EnumSetting U() {
      return (EnumSetting)this.t();
   }

   private int W() {
      Enum[] enums = (Enum[])this.U().getValue().getClass().getEnumConstants();

      for (int i = 0; i < enums.length; i++) {
         if (enums[i] == this.U().getValue()) {
            return i;
         }
      }

      return -1;
   }
}
