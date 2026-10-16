package bypass.module.combat;

public enum KillAuraAutoBlockMode {
   NONE,
   BLOCK,
   INTERACT,
   WATCHDOG,
   WATCHDOG2,
   ALWAYS,
   FAKE;

   private static final KillAuraAutoBlockMode[] VALUES = new KillAuraAutoBlockMode[]{NONE, BLOCK, INTERACT, WATCHDOG, WATCHDOG2, ALWAYS, FAKE};
}
