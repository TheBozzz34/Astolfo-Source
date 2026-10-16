package bypass.module.other;

public enum AnnoyMode {
   CRAZY("Crazy"),
   WITCH("Witch"),
   DEATH("Death");

   private String label;
   private static final AnnoyMode[] VALUES = new AnnoyMode[]{CRAZY, WITCH, DEATH};

   AnnoyMode(String string2) {
      this.label = string2;
   }

   public static String getLabel(AnnoyMode annoyMode) {
      return annoyMode.label;
   }
}
