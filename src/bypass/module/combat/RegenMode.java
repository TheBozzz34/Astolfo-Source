package bypass.module.combat;

public enum RegenMode {
   VANILLA("Vanilla"),
   POTION("Potion"),
   WAIT("Wait");

   private String w;
   private static final RegenMode[] m = new RegenMode[]{VANILLA, POTION, WAIT};

   RegenMode(String string2) {
      this.w = string2;
   }

   public static String S(RegenMode regenMode) {
      return regenMode.w;
   }
}
