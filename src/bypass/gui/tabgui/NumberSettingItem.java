package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.setting.NumberSetting;
import bypass.util.MathUtil;
import java.text.DecimalFormat;
import net.minecraft.client.Minecraft;

public class NumberSettingItem extends TabGuiItem {
   private NumberSetting e;
   private static final long b = 74022487267985L;

   public NumberSettingItem(TabGui.Panel panel, NumberSetting numberSetting) {
      super(panel);
      this.e = numberSetting;
   }

   @Override
   public void z(int i) {
      super.z(i);
      if (i == 78) {
         DecimalFormat decimalFormat = new DecimalFormat("0.0");
         if (this.e.valueClass == Integer.class) {
            int j = Integer.parseInt(String.valueOf(this.c() + (Minecraft.getMinecraft().gameSettings.Ze.isKeyDown() ? 10 : 1)));
            this.e.setValue(j);
         } else if (this.e.valueClass == Double.class) {
            double d = Double.parseDouble(decimalFormat.format(this.u() + 0.1 * (Minecraft.getMinecraft().gameSettings.Ze.isKeyDown() ? 10.0 : 1.0)));
            this.e.setValue(d);
         } else {
            float f = Float.parseFloat(decimalFormat.format(this.b() + 0.1F * (Minecraft.getMinecraft().gameSettings.Ze.isKeyDown() ? 10.0F : 1.0F)));
            this.e.setValue(f);
         }
      } else if (i == 74) {
         DecimalFormat decimalFormat2 = new DecimalFormat("0.0");
         if (this.e.valueClass == Integer.class) {
            int k = Integer.parseInt(String.valueOf(this.c() - (Minecraft.getMinecraft().gameSettings.Ze.isKeyDown() ? 10 : 1)));
            this.e.setValue(k);
         } else if (this.e.valueClass == Double.class) {
            double d2 = Double.parseDouble(decimalFormat2.format(this.u() - 0.1 * (Minecraft.getMinecraft().gameSettings.Ze.isKeyDown() ? 10.0 : 1.0)));
            this.e.setValue(d2);
         } else {
            float g = Float.parseFloat(decimalFormat2.format(this.b() - 0.1F * (Minecraft.getMinecraft().gameSettings.Ze.isKeyDown() ? 10.0F : 1.0F)));
            this.e.setValue(g);
         }
      }
   }

   public int c() {
      String string = MathUtil.ONE_DECIMAL.format(this.e.getValue()).replace(",", "");
      return Integer.valueOf(string);
   }

   public float b() {
      String string = MathUtil.ONE_DECIMAL.format(this.e.getValue()).replace(",", ".");
      return Float.parseFloat(string);
   }

   public double u() {
      String string = MathUtil.ONE_DECIMAL.format(this.e.getValue()).replace(",", ".");
      return Double.parseDouble(string);
   }

   @Override
   public String L() {
      return "§f" + this.e.getName() + ": §7" + this.e.getValue();
   }
}
