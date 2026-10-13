package bypass.module.combat;

public enum RegenMode {
   VANILLA("Vanilla"),
   POTION("Potion"),
   WAIT("Wait");

   private String w;
   private static final RegenMode[] m = new RegenMode[]{VANILLA, POTION, WAIT};

   RegenMode(String var3) {
      this.w = var3;
   }

   public static String S(RegenMode var0) {
      return var0.w;
   }
}
