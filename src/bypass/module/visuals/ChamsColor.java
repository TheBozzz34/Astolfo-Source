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

   ChamsColor(Color color) {
      this.x = color;
   }

   public void o(Color color) {
      this.x = color;
   }

   public Color d() {
      if (this.x == null) {
         return this.X(Minecraft.getSystemTime(), 1.0F);
      } else {
         return this == DEFAULT ? Astolfo.INSTANCE.getColorSetting().getValue() : this.x;
      }
   }

   private Color X(long l, float f) {
      float g = 1.0F - MathHelper.abs(MathHelper.sin((float)l % 4500.0F / 4500.0F * (float) Math.PI * 2.0F) * 0.5F);
      long m = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(g, 0.5F, 1.0F)), 16);
      Color color = new Color((int)m);
      return new Color(color.getRed() / 255.0F * f, color.getGreen() / 255.0F * f, color.getBlue() / 255.0F * f, color.getAlpha() / 255.0F);
   }
}
