package bypass.module.combat;

public enum KillAuraAutoBlockMode {
   NONE,
   BLOCK,
   INTERACT,
   WATCHDOG,
   WATCHDOG2,
   ALWAYS,
   FAKE;

   private static final KillAuraAutoBlockMode[] L = new KillAuraAutoBlockMode[]{NONE, BLOCK, INTERACT, WATCHDOG, WATCHDOG2, ALWAYS, FAKE};
}
