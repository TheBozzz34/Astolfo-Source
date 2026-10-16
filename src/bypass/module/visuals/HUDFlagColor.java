package bypass.module.visuals;

import java.awt.Color;

public enum HUDFlagColor {
   BLUE(new Color(85, 205, 252)),
   WHITE(Color.WHITE),
   PINK(new Color(247, 168, 184));

   private Color color;
   private static final HUDFlagColor[] VALUES = new HUDFlagColor[]{BLUE, WHITE, PINK};

   HUDFlagColor(Color color) {
      this.color = color;
   }

   public Color getColor() {
      return this.color;
   }

   public static Color colorOf(HUDFlagColor hudFlagColor) {
      return hudFlagColor.color;
   }
}
