package bypass.module.movement;

public enum NoSlowMode {
   VANILLA("Vanilla"),
   WATCHDOG("Watchdog"),
   NCP("NCP");

   private String p;
   private static final NoSlowMode[] R = new NoSlowMode[]{VANILLA, WATCHDOG, NCP};

   NoSlowMode(String string2) {
      this.p = string2;
   }

   public static String t(NoSlowMode noSlowMode) {
      return noSlowMode.p;
   }
}
