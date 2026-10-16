package bypass.render;

import bypass.Astolfo;
import java.awt.Color;

public enum ClientColor {
   DEFAULT(Astolfo.INSTANCE.getColorSetting().getValue()),
   PINK(new Color(255, 127, 209)),
   PURPLE(new Color(167, 120, 255)),
   RED(new Color(231, 76, 60)),
   GREEN(new Color(46, 204, 113)),
   BLUE(new Color(85, 205, 252)),
   ORANGE(new Color(243, 156, 18)),
   WHITE(Color.WHITE);

   private Color color;

   ClientColor(Color color) {
      this.color = color;
   }

   public void setColor(Color color) {
      this.color = color;
   }

   public Color getColor() {
      return this == DEFAULT ? Astolfo.INSTANCE.getColorSetting().getValue() : this.color;
   }
}
