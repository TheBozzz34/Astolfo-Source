package bypass.module.visuals;

import java.awt.Color;

public enum HUDFlagColor {
   BLUE(new Color(85, 205, 252)),
   WHITE(Color.WHITE),
   PINK(new Color(247, 168, 184));

   private Color j;
   private static final HUDFlagColor[] P = new HUDFlagColor[]{BLUE, WHITE, PINK};

   HUDFlagColor(Color var3) {
      this.j = var3;
   }

   public Color m() {
      return this.j;
   }

   public static Color s(HUDFlagColor var0) {
      return var0.j;
   }
}
