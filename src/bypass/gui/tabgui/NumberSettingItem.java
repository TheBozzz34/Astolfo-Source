package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.setting.NumberSetting;
import bypass.util.MathUtil;
import java.text.DecimalFormat;
import net.minecraft.client.Minecraft;

public class NumberSettingItem extends TabGuiItem {
   private NumberSetting e;
   private static final long b = 74022487267985L;

   public NumberSettingItem(TabGui.Panel var1, NumberSetting var2) {
      super(var1);
      this.e = var2;
   }

   @Override
   public void z(int var1) {
      super.z(var1);
      if (var1 == 78) {
         DecimalFormat var5 = new DecimalFormat("0.0");
         if (this.e.valueClass == Integer.class) {
            int var6 = Integer.parseInt(String.valueOf(this.c() + (Minecraft.getMinecraft().gameSettings.Ze.isKeyDown() ? 10 : 1)));
            this.e.setValue(var6);
         } else if (this.e.valueClass == Double.class) {
            double var9 = Double.parseDouble(var5.format(this.u() + 0.1 * (Minecraft.getMinecraft().gameSettings.Ze.isKeyDown() ? 10.0 : 1.0)));
            this.e.setValue(var9);
         } else {
            float var10 = Float.parseFloat(var5.format(this.b() + 0.1F * (Minecraft.getMinecraft().gameSettings.Ze.isKeyDown() ? 10.0F : 1.0F)));
            this.e.setValue(var10);
         }
      } else if (var1 == 74) {
         DecimalFormat var8 = new DecimalFormat("0.0");
         if (this.e.valueClass == Integer.class) {
            int var11 = Integer.parseInt(String.valueOf(this.c() - (Minecraft.getMinecraft().gameSettings.Ze.isKeyDown() ? 10 : 1)));
            this.e.setValue(var11);
         } else if (this.e.valueClass == Double.class) {
            double var12 = Double.parseDouble(var8.format(this.u() - 0.1 * (Minecraft.getMinecraft().gameSettings.Ze.isKeyDown() ? 10.0 : 1.0)));
            this.e.setValue(var12);
         } else {
            float var13 = Float.parseFloat(var8.format(this.b() - 0.1F * (Minecraft.getMinecraft().gameSettings.Ze.isKeyDown() ? 10.0F : 1.0F)));
            this.e.setValue(var13);
         }
      }
   }

   public int c() {
      String var1 = MathUtil.ONE_DECIMAL.format(this.e.getValue()).replace(",", "");
      return Integer.valueOf(var1);
   }

   public float b() {
      String var1 = MathUtil.ONE_DECIMAL.format(this.e.getValue()).replace(",", ".");
      return Float.parseFloat(var1);
   }

   public double u() {
      String var1 = MathUtil.ONE_DECIMAL.format(this.e.getValue()).replace(",", ".");
      return Double.parseDouble(var1);
   }

   @Override
   public String L() {
      return "§f" + this.e.getName() + ": §7" + this.e.getValue();
   }
}
