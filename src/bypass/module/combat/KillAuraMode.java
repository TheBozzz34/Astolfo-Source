package bypass.module.combat;

public enum KillAuraMode {
   SINGLE("Single"),
   SWITCH("Switch"),
   MULTI("Multi"),
   TP("TP");

   private String w;
   private static final KillAuraMode[] K = new KillAuraMode[]{SINGLE, SWITCH, MULTI, TP};

   KillAuraMode(String string2) {
      this.w = string2;
   }

   public static String D(KillAuraMode killAuraMode) {
      return killAuraMode.w;
   }
}
