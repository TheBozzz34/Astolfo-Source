package bypass.module.movement;

public enum NoSlowMode {
   VANILLA("Vanilla"),
   WATCHDOG("Watchdog"),
   NCP("NCP");

   private String p;
   private static final NoSlowMode[] R = new NoSlowMode[]{VANILLA, WATCHDOG, NCP};

   NoSlowMode(String var3) {
      this.p = var3;
   }

   public static String t(NoSlowMode var0) {
      return var0.p;
   }
}
