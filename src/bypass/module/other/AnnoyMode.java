package bypass.module.other;

public enum AnnoyMode {
   CRAZY("Crazy"),
   WITCH("Witch"),
   DEATH("Death");

   private String e;
   private static final AnnoyMode[] a = new AnnoyMode[]{CRAZY, WITCH, DEATH};

   AnnoyMode(String string2) {
      this.e = string2;
   }

   public static String u(AnnoyMode annoyMode) {
      return annoyMode.e;
   }
}
