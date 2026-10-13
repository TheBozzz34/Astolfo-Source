package bypass.module.other;

public enum AnnoyMode {
   CRAZY("Crazy"),
   WITCH("Witch"),
   DEATH("Death");

   private String e;
   private static final AnnoyMode[] a = new AnnoyMode[]{CRAZY, WITCH, DEATH};

   AnnoyMode(String var3) {
      this.e = var3;
   }

   public static String u(AnnoyMode var0) {
      return var0.e;
   }
}
