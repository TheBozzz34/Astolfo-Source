package bypass.gui.clickgui.defaultgui;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.setting.EnumSetting;
import bypass.setting.Setting;
import net.minecraft.client.gui.GuiMainMenu;

public class EnumComponent extends SettingComponent {
   private static final long c = 5155583939849L;
   private static final long d = -7993921108442297140L;

   public EnumComponent(Container var1, Setting var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public void render(int var1, int var2) {
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
   public void l(int var1) {
      if (this.I().s_boolean()) {
         Enum[] var5 = (Enum[])this.U().getValue().getClass().getEnumConstants();
         int var6 = this.W();
         var6 += var1 == 0 ? 1 : -1;
         if (var6 >= var5.length || var6 < 0) {
            var6 = 0;
         }

         this.U().setValue(var5[var6]);
      }
   }

   private EnumSetting U() {
      return (EnumSetting)this.t();
   }

   private int W() {
      Enum[] var4 = (Enum[])this.U().getValue().getClass().getEnumConstants();

      for (int var5 = 0; var5 < var4.length; var5++) {
         if (var4[var5] == this.U().getValue()) {
            return var5;
         }
      }

      return -1;
   }
}
