package bypass.module.combat;

public enum FastBowMode {
   VANILLA("Vanilla");

   private String z;
   private static final FastBowMode[] s = new FastBowMode[]{VANILLA};

   FastBowMode(String var3) {
      this.z = var3;
   }

   public static String T(FastBowMode var0) {
      return var0.z;
   }
}
