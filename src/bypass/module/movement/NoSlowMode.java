package bypass.module.movement;

public enum NoSlowMode {
   VANILLA("Vanilla"),
   WATCHDOG("Watchdog"),
   NCP("NCP");

   private String displayName;
   private static final NoSlowMode[] VALUES = new NoSlowMode[]{VANILLA, WATCHDOG, NCP};

   NoSlowMode(String string2) {
      this.displayName = string2;
   }

   public static String displayNameOf(NoSlowMode noSlowMode) {
      return noSlowMode.displayName;
   }
}
