package bypass.render;

import java.awt.Color;

public enum BaseColor {
   PINK(new Color(255, 127, 209)),
   PURPLE(new Color(164, 54, 145, 255)),
   RED(new Color(231, 76, 60)),
   GREEN(new Color(46, 204, 113)),
   BLUE(new Color(126, 180, 255)),
   ORANGE(new Color(243, 156, 18)),
   WHITE(Color.WHITE);

   private Color color;

   BaseColor(Color color) {
      this.color = color;
   }

   public Color getColor() {
      return this.color;
   }
}
