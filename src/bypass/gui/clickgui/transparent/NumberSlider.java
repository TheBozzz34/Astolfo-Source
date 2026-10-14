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
   private final DecimalFormat n = new DecimalFormat("#");
   private static final Color Y = new Color(22, 22, 22);

   public NumberSlider(Container container, Setting setting, int i, int j) {
      super(container, setting, i, j);
   }

   @Override
   public void render(int i, int j) {
      float f = this.o().getMax().floatValue() - this.o().getMin().floatValue();
      int k = this.o().getMax().intValue() - this.o().getMin().intValue();
      float g = Math.round((this.C() - this.o().getMin().floatValue()) / (this.o().valueClass == Integer.class ? k : f) * this.x());
      RenderUtil.drawRect(
         this.M() + 1.5F, this.E_float() - 1.0F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + 4.0F, new Color(1, 1, 1, 120).getRGB()
      );
      RenderUtil.drawRect(this.M() + 1.5F, this.E_float() + 10.0F, this.M() + g, this.E_float() + this.Y() + 2.0F, client.getColor().getRGB());
      String string;
      if (this.o().valueClass.equals(Integer.class)) {
         string = String.format("%s %s", this.o().getName().replace("-", " "), this.h());
      } else if (this.o().valueClass.equals(Float.class)) {
         string = String.format("%s %s", this.o().getName().replace("-", " "), this.C());
      } else if (this.o().valueClass.equals(Double.class)) {
         string = String.format("%s %s", this.o().getName().replace("-", " "), this.j());
      } else {
         string = String.format("%s %s", this.o().getName().replace("-", " "), this.h());
      }

      string = string + this.o().getSuffix();
      client.getCustomFontRenderer().drawStringWithShadow(EnumChatFormatting.GRAY + string, this.M() + 4.0F, this.E_float() - 1.0F, Color.WHITE.getRGB());
      if (this.G(i, j) && HUD.toolTips.getValue()) {
         client.getCustomFontRenderer().drawStringWithShadow(this.o().getDescription(), i + 6, j - 4, -1);
      }

      if (this.G(i, j) && Mouse.isButtonDown(0) && this.I().s_boolean()) {
         if (this.o().valueClass == Integer.class) {
            this.o().setValue((i - (int)this.M()) * this.o().getMax().intValue() / this.x() + this.o().getMin().intValue());
         } else if (this.o().valueClass == Double.class) {
            this.o()
               .setValue(Double.valueOf(MathUtil.ONE_DECIMAL.format((i - this.M()) * f / this.x() + this.o().getMin().doubleValue()).replace(",", ".")));
         } else {
            this.o().setValue(Float.valueOf(MathUtil.ONE_DECIMAL.format((i - this.M()) * f / this.x() + this.o().getMin().floatValue()).replace(",", ".")));
         }
      }
   }

   public int h() {
      String string = MathUtil.ONE_DECIMAL.format(this.o().getValue()).replace(",", "");
      return Integer.valueOf(string);
   }

   public float C() {
      String string = MathUtil.ONE_DECIMAL.format(this.o().getValue()).replace(",", ".");
      return Float.parseFloat(string);
   }

   public double j() {
      String string = MathUtil.ONE_DECIMAL.format(this.o().getValue()).replace(",", ".");
      return Double.parseDouble(string);
   }

   private NumberSetting o() {
      return (NumberSetting)this.t();
   }

   @Override
   public void l(int i) {
   }
}
