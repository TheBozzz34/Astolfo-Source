package bypass.gui.tabgui;

import bypass.gui.TabGui;
import bypass.setting.NumberSetting;
import bypass.util.MathUtil;
import java.text.DecimalFormat;
import net.minecraft.client.Minecraft;

public class NumberSettingItem extends TabGuiItem {
   private NumberSetting setting;

   public NumberSettingItem(TabGui.Panel panel, NumberSetting numberSetting) {
      super(panel);
      this.setting = numberSetting;
   }

   @Override
   public void handleKeyPress(int i) {
      super.handleKeyPress(i);
      if (i == 78) {
         DecimalFormat decimalFormat = new DecimalFormat("0.0");
         if (this.setting.valueClass == Integer.class) {
            int j = Integer.parseInt(String.valueOf(this.getIntValue() + (Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown() ? 10 : 1)));
            this.setting.setValue(j);
         } else if (this.setting.valueClass == Double.class) {
            double d = Double.parseDouble(
               decimalFormat.format(this.getDoubleValue() + 0.1 * (Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown() ? 10.0 : 1.0))
            );
            this.setting.setValue(d);
         } else {
            float f = Float.parseFloat(
               decimalFormat.format(this.getFloatValue() + 0.1F * (Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown() ? 10.0F : 1.0F))
            );
            this.setting.setValue(f);
         }
      } else if (i == 74) {
         DecimalFormat decimalFormat2 = new DecimalFormat("0.0");
         if (this.setting.valueClass == Integer.class) {
            int k = Integer.parseInt(String.valueOf(this.getIntValue() - (Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown() ? 10 : 1)));
            this.setting.setValue(k);
         } else if (this.setting.valueClass == Double.class) {
            double e = Double.parseDouble(
               decimalFormat2.format(this.getDoubleValue() - 0.1 * (Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown() ? 10.0 : 1.0))
            );
            this.setting.setValue(e);
         } else {
            float g = Float.parseFloat(
               decimalFormat2.format(this.getFloatValue() - 0.1F * (Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown() ? 10.0F : 1.0F))
            );
            this.setting.setValue(g);
         }
      }
   }

   public int getIntValue() {
      String string = MathUtil.ONE_DECIMAL.format(this.setting.getValue()).replace(",", "");
      return Integer.valueOf(string);
   }

   public float getFloatValue() {
      String string = MathUtil.ONE_DECIMAL.format(this.setting.getValue()).replace(",", ".");
      return Float.parseFloat(string);
   }

   public double getDoubleValue() {
      String string = MathUtil.ONE_DECIMAL.format(this.setting.getValue()).replace(",", ".");
      return Double.parseDouble(string);
   }

   @Override
   public String getDisplayText() {
      return "§f" + this.setting.getName() + ": §7" + this.setting.getValue();
   }
}
