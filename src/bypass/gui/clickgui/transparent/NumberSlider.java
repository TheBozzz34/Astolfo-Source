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
   private static final long c = 17220884941168L;

   public NumberSlider(Container var1, Setting var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public void render(int var1, int var2) {
      long var3 = c ^ 27102496675586L;
      long var5 = var3 ^ 39308001293406L;
      float var8 = this.o().getMax().floatValue() - this.o().getMin().floatValue();
      int var9 = this.o().getMax().intValue() - this.o().getMin().intValue();
      float var10 = Math.round((this.C() - this.o().getMin().floatValue()) / (this.o().valueClass == Integer.class ? var9 : var8) * this.x());
      RenderUtil.drawRect(
         this.M() + 1.5F, this.E_float() - 1.0F, this.M() + this.x() + 0.5F, this.E_float() + this.Y() + 4.0F, new Color(1, 1, 1, 120).getRGB()
      );
      RenderUtil.drawRect(this.M() + 1.5F, this.E_float() + 10.0F, this.M() + var10, this.E_float() + this.Y() + 2.0F, client.getColor().getRGB());
      String var11;
      if (this.o().valueClass.equals(Integer.class)) {
         var11 = String.format("%s %s", this.o().getName().replace("-", " "), this.h());
      } else if (this.o().valueClass.equals(Float.class)) {
         var11 = String.format("%s %s", this.o().getName().replace("-", " "), this.C());
      } else if (this.o().valueClass.equals(Double.class)) {
         var11 = String.format("%s %s", this.o().getName().replace("-", " "), this.j());
      } else {
         var11 = String.format("%s %s", this.o().getName().replace("-", " "), this.h());
      }

      var11 = var11 + this.o().getSuffix();
      client.getCustomFontRenderer().drawStringWithShadow(EnumChatFormatting.GRAY + var11, this.M() + 4.0F, this.E_float() - 1.0F, Color.WHITE.getRGB());
      if (this.G(var1, var2) && HUD.toolTips.getValue()) {
         client.getCustomFontRenderer().drawStringWithShadow(this.o().getDescription(), var1 + 6, var2 - 4, -1);
      }

      if (this.G(var1, var2) && Mouse.isButtonDown(0) && this.I().s_boolean()) {
         if (this.o().valueClass == Integer.class) {
            this.o().setValue((var1 - (int)this.M()) * this.o().getMax().intValue() / this.x() + this.o().getMin().intValue());
         } else if (this.o().valueClass == Double.class) {
            this.o()
               .setValue(Double.valueOf(MathUtil.ONE_DECIMAL.format((var1 - this.M()) * var8 / this.x() + this.o().getMin().doubleValue()).replace(",", ".")));
         } else {
            this.o()
               .setValue(Float.valueOf(MathUtil.ONE_DECIMAL.format((var1 - this.M()) * var8 / this.x() + this.o().getMin().floatValue()).replace(",", ".")));
         }
      }
   }

   public int h() {
      String var1 = MathUtil.ONE_DECIMAL.format(this.o().getValue()).replace(",", "");
      return Integer.valueOf(var1);
   }

   public float C() {
      String var1 = MathUtil.ONE_DECIMAL.format(this.o().getValue()).replace(",", ".");
      return Float.parseFloat(var1);
   }

   public double j() {
      String var1 = MathUtil.ONE_DECIMAL.format(this.o().getValue()).replace(",", ".");
      return Double.parseDouble(var1);
   }

   private NumberSetting o() {
      return (NumberSetting)this.t();
   }

   @Override
   public void l(int var1) {
   }
}
