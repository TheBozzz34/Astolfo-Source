package bypass.module.combat;

public enum KillAuraMode {
   SINGLE("Single"),
   SWITCH("Switch"),
   MULTI("Multi"),
   TP("TP");

   private String w;
   private static final KillAuraMode[] K = new KillAuraMode[]{SINGLE, SWITCH, MULTI, TP};

   KillAuraMode(String var3) {
      this.w = var3;
   }

   public static String D(KillAuraMode var0) {
      return var0.w;
   }
}
