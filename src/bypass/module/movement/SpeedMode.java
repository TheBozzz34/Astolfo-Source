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

   private final String displayName;
   private static final SpeedMode[] VALUES = new SpeedMode[]{BHOP, WATCHDOG, VANILLA_BHOP, AAC_3_BHOP, GWEN, SENTINEL, HIGH_JUMP, TELEPORT, SQUADS, NCP, YPORT};

   SpeedMode(String string2) {
      this.displayName = string2;
   }

   public static String displayNameOf(SpeedMode speedMode) {
      return speedMode.displayName;
   }
}
