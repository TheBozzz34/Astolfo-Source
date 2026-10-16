package bypass.module.combat;

public enum RegenMode {
   VANILLA("Vanilla"),
   POTION("Potion"),
   WAIT("Wait");

   private String displayName;
   private static final RegenMode[] VALUES = new RegenMode[]{VANILLA, POTION, WAIT};

   RegenMode(String string2) {
      this.displayName = string2;
   }

   public static String displayNameOf(RegenMode regenMode) {
      return regenMode.displayName;
   }
}
