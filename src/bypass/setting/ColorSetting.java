package bypass.setting;

import bypass.gui.clickgui.defaultgui.ColorPicker;
import bypass.module.hidden.HypixelPacketHandler;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public final class ColorSetting extends Setting<Color> {
   public ColorMode mode = ColorMode.NONE;
   private static final long c = 81396794311465L;
   private static final long g = -2662563648286752513L;

   public ColorSetting(String var1, String var2, Color var3) {
      super(var1, var2, var3, SettingType.COLOR);
   }

   public Color getValue() {
      Color var3 = (Color)super.getValue();
      switch (this.mode) {
         case PULSATE:
            float var4 = 1.0F - MathHelper.abs(MathHelper.sin((float)Minecraft.getSystemTime() % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.6F);
            return new Color(var3.getRed(), var3.getGreen(), var3.getBlue(), MathHelper.clamp_int((int)(var3.getAlpha() * var4), 0, (int)g));
         case RAINBOW:
            return ColorPicker.h(HypixelPacketHandler.r);
         default:
            return var3;
      }
   }

   public void setValue(Color var1) {
      super.setValue(var1);
   }

   @Override
   public void parse(String var1) {
      String[] var5 = var1.split(" ");
      if (var5.length == 3) {
         this.setValue(new Color(Integer.parseInt(var5[0]), Integer.parseInt(var5[1]), Integer.parseInt(var5[2])));
      } else if (var5.length == 4) {
         this.setValue(new Color(Integer.parseInt(var5[0]), Integer.parseInt(var5[1]), Integer.parseInt(var5[2]), Integer.parseInt(var5[3])));
      } else if (var5.length == 5) {
         this.mode = ColorMode.values()[Integer.parseInt(var5[4])];
         this.setValue(new Color(Integer.parseInt(var5[0]), Integer.parseInt(var5[1]), Integer.parseInt(var5[2]), Integer.parseInt(var5[3])));
      }
   }

   @Override
   public String asString() {
      return this.value.getRed() + " " + this.value.getGreen() + " " + this.value.getBlue() + " " + this.value.getAlpha() + " " + this.mode.index;
   }
}
