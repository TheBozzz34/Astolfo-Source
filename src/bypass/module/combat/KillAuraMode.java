package bypass.module.combat;

public enum KillAuraMode {
   SINGLE("Single"),
   SWITCH("Switch"),
   MULTI("Multi"),
   TP("TP");

   private String displayName;
   private static final KillAuraMode[] VALUES = new KillAuraMode[]{SINGLE, SWITCH, MULTI, TP};

   KillAuraMode(String string2) {
      this.displayName = string2;
   }

   public static String displayNameOf(KillAuraMode killAuraMode) {
      return killAuraMode.displayName;
   }
}
