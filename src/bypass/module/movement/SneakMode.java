package bypass.module.movement;

public enum SneakMode {
   WATCHDOG("Watchdog"),
   ALWAYS("Always");

   private String U;
   private static final SneakMode[] x = new SneakMode[]{WATCHDOG, ALWAYS};

   SneakMode(String var3) {
      this.U = var3;
   }

   public static String T(SneakMode var0) {
      return var0.U;
   }
}
