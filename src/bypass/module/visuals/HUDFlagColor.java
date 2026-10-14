package bypass.module.visuals;

import java.awt.Color;

public enum HUDFlagColor {
   BLUE(new Color(85, 205, 252)),
   WHITE(Color.WHITE),
   PINK(new Color(247, 168, 184));

   private Color j;
   private static final HUDFlagColor[] P = new HUDFlagColor[]{BLUE, WHITE, PINK};

   HUDFlagColor(Color color) {
      this.j = color;
   }

   public Color m() {
      return this.j;
   }

   public static Color s(HUDFlagColor hudFlagColor) {
      return hudFlagColor.j;
   }
}
