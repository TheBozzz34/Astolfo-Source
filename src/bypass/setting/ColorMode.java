package bypass.setting;

public enum ColorMode {
   NONE(0),
   RAINBOW(1),
   PULSATE(2);

   public final int index;
   private static final ColorMode[] I = new ColorMode[]{NONE, RAINBOW, PULSATE};

   ColorMode(int j) {
      this.index = j;
   }
}
