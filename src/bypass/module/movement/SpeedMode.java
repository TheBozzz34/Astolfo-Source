package bypass.module.movement;

public enum SpeedMode {
   BHOP("Bhop"),
   WATCHDOG("Watchdog"),
   VANILLA_BHOP("Vanilla Bhop"),
   AAC_3_BHOP("AAC 3.3 Hop"),
   GWEN("GWEN"),
   SENTINEL("Sentinel"),
   HIGH_JUMP("High Jump"),
   TELEPORT("Teleport"),
   SQUADS("Squads"),
   NCP("NCP"),
   YPORT("Y Port");

   private final String d;
   private static final SpeedMode[] y = new SpeedMode[]{BHOP, WATCHDOG, VANILLA_BHOP, AAC_3_BHOP, GWEN, SENTINEL, HIGH_JUMP, TELEPORT, SQUADS, NCP, YPORT};

   SpeedMode(String var3) {
      this.d = var3;
   }

   public static String K(SpeedMode var0) {
      return var0.d;
   }
}
