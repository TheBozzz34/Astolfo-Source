package bypass.module.visuals;

import bypass.Astolfo;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public enum ChamsColor {
   DEFAULT(Astolfo.INSTANCE.getColorSetting().getValue()),
   PINK(new Color(255, 127, 209)),
   PURPLE(new Color(167, 120, 255)),
   RED(new Color(231, 76, 60)),
   GREEN(new Color(46, 204, 113)),
   BLUE(new Color(85, 205, 252)),
   ORANGE(new Color(243, 156, 18)),
   RAINBOW(null),
   WHITE(Color.WHITE);

   private Color x;
   private static final ChamsColor[] S = new ChamsColor[]{DEFAULT, PINK, PURPLE, RED, GREEN, BLUE, ORANGE, RAINBOW, WHITE};
   private static final long a = 17969618905453L;

   ChamsColor(Color var3) {
      this.x = var3;
   }

   public void o(Color var1) {
      this.x = var1;
   }

   public Color d() {
      if (this.x == null) {
         return this.X(Minecraft.getSystemTime(), 1.0F);
      } else {
         return this == DEFAULT ? Astolfo.INSTANCE.getColorSetting().getValue() : this.x;
      }
   }

   private Color X(long var1, float var3) {
      float var6 = 1.0F - MathHelper.abs(MathHelper.sin((float)var1 % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.5F);
      long var7 = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(var6, 0.5F, 1.0F)), 16);
      Color var9 = new Color((int)var7);
      return new Color(var9.getRed() / 255.0F * var3, var9.getGreen() / 255.0F * var3, var9.getBlue() / 255.0F * var3, var9.getAlpha() / 255.0F);
   }
}
