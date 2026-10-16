package bypass.gui.clickgui.transparent;

import bypass.gui.clickgui.SettingComponent;
import bypass.gui.component.Container;
import bypass.module.visuals.HUD;
import bypass.render.RenderUtil;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;
import bypass.util.MathUtil;
import java.awt.Color;
import java.text.DecimalFormat;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;

public class NumberSlider extends SettingComponent {
   private final DecimalFormat decimalFormat = new DecimalFormat("#");
   private static final Color darkColor = new Color(22, 22, 22);

   public NumberSlider(Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
   }

   @Override
   public void render(int i, int j) {
      float f = this.getNumberSetting().getMax().floatValue() - this.getNumberSetting().getMin().floatValue();
      int k = this.getNumberSetting().getMax().intValue() - this.getNumberSetting().getMin().intValue();
      float g = Math.round(
         (this.getRoundedFloat() - this.getNumberSetting().getMin().floatValue())
            / (this.getNumberSetting().valueClass == Integer.class ? k : f)
            * this.getWidth()
      );
      RenderUtil.drawRect(
         this.getX() + 1.5F,
         this.getYPos() - 1.0F,
         this.getX() + this.getWidth() + 0.5F,
         this.getYPos() + this.getHeight() + 4.0F,
         new Color(1, 1, 1, 120).getRGB()
      );
      RenderUtil.drawRect(this.getX() + 1.5F, this.getYPos() + 10.0F, this.getX() + g, this.getYPos() + this.getHeight() + 2.0F, client.getColor().getRGB());
      String string;
      if (this.getNumberSetting().valueClass.equals(Integer.class)) {
         string = String.format("%s %s", this.getNumberSetting().getName().replace("-", " "), this.getRoundedInt());
      } else if (this.getNumberSetting().valueClass.equals(Float.class)) {
         string = String.format("%s %s", this.getNumberSetting().getName().replace("-", " "), this.getRoundedFloat());
      } else if (this.getNumberSetting().valueClass.equals(Double.class)) {
         string = String.format("%s %s", this.getNumberSetting().getName().replace("-", " "), this.getRoundedDouble());
      } else {
         string = String.format("%s %s", this.getNumberSetting().getName().replace("-", " "), this.getRoundedInt());
      }

      string = string + this.getNumberSetting().getSuffix();
      client.getCustomFontRenderer().drawStringWithShadow(EnumChatFormatting.GRAY + string, this.getX() + 4.0F, this.getYPos() - 1.0F, Color.WHITE.getRGB());
      if (this.isHovered(i, j) && HUD.toolTips.getValue()) {
         client.getCustomFontRenderer().drawStringWithShadow(this.getNumberSetting().getDescription(), i + 6, j - 4, -1);
      }

      if (this.isHovered(i, j) && Mouse.isButtonDown(0) && this.getParentContainer().isExpanded()) {
         if (this.getNumberSetting().valueClass == Integer.class) {
            this.getNumberSetting()
               .setValue((i - (int)this.getX()) * this.getNumberSetting().getMax().intValue() / this.getWidth() + this.getNumberSetting().getMin().intValue());
         } else if (this.getNumberSetting().valueClass == Double.class) {
            this.getNumberSetting()
               .setValue(
                  Double.valueOf(
                     MathUtil.ONE_DECIMAL.format((i - this.getX()) * f / this.getWidth() + this.getNumberSetting().getMin().doubleValue()).replace(",", ".")
                  )
               );
         } else {
            this.getNumberSetting()
               .setValue(
                  Float.valueOf(
                     MathUtil.ONE_DECIMAL.format((i - this.getX()) * f / this.getWidth() + this.getNumberSetting().getMin().floatValue()).replace(",", ".")
                  )
               );
         }
      }
   }

   public int getRoundedInt() {
      String string = MathUtil.ONE_DECIMAL.format(this.getNumberSetting().getValue()).replace(",", "");
      return Integer.valueOf(string);
   }

   public float getRoundedFloat() {
      String string = MathUtil.ONE_DECIMAL.format(this.getNumberSetting().getValue()).replace(",", ".");
      return Float.parseFloat(string);
   }

   public double getRoundedDouble() {
      String string = MathUtil.ONE_DECIMAL.format(this.getNumberSetting().getValue()).replace(",", ".");
      return Double.parseDouble(string);
   }

   private NumberSetting getNumberSetting() {
      return (NumberSetting)this.getSetting();
   }

   @Override
   public void onClick(int i) {
   }
}
