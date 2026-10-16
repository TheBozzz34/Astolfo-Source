package bypass.module.visuals;

import bypass.Astolfo;
import java.awt.Color;

public enum OutlineColor {
   DEFAULT(Astolfo.INSTANCE.getColorSetting().getValue()),
   PINK(new Color(255, 127, 209)),
   PURPLE(new Color(167, 120, 255)),
   RED(new Color(231, 76, 60)),
   GREEN(new Color(46, 204, 113)),
   BLUE(new Color(85, 205, 252)),
   ORANGE(new Color(243, 156, 18)),
   RAINBOW(null),
   WHITE(Color.WHITE);

   private Color color;
   private static final OutlineColor[] VALUES = new OutlineColor[]{DEFAULT, PINK, PURPLE, RED, GREEN, BLUE, ORANGE, RAINBOW, WHITE};

   OutlineColor(Color color) {
      this.color = color;
   }

   public void setColor(Color color) {
      this.color = color;
   }

   public Color getColor() {
      return this.color;
   }
}
