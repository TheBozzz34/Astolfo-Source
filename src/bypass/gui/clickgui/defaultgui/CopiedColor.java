package bypass.gui.clickgui.defaultgui;

import java.awt.Color;

public class CopiedColor {
   private final Color color;
   private final int saturationOffset;
   private final int brightnessOffset;
   private final int hueOffset;
   private final int alphaOffset;
   private final int modeOrdinal;

   public CopiedColor(int i, Color color, int j, int k, int l, int m) {
      this.modeOrdinal = i;
      this.color = color;
      this.saturationOffset = j;
      this.brightnessOffset = k;
      this.hueOffset = l;
      this.alphaOffset = m;
   }

   public int getModeOrdinal() {
      return this.modeOrdinal;
   }

   public Color getColor() {
      return this.color;
   }

   public int getHueOffset() {
      return this.hueOffset;
   }

   public int getSaturationOffset() {
      return this.saturationOffset;
   }

   public int getBrightnessOffset() {
      return this.brightnessOffset;
   }

   public int getAlphaOffset() {
      return this.alphaOffset;
   }
}
