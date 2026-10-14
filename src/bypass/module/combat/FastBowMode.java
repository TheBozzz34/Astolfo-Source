package bypass.module.combat;

public enum FastBowMode {
   VANILLA("Vanilla");

   private String z;
   private static final FastBowMode[] s = new FastBowMode[]{VANILLA};

   FastBowMode(String string2) {
      this.z = string2;
   }

   public static String T(FastBowMode fastBowMode) {
      return fastBowMode.z;
   }
}
