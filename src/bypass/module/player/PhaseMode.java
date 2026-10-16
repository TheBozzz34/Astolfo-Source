package bypass.module.player;

public enum PhaseMode {
   VERTICAL("Vertical"),
   FULLBLOCK("Full Block"),
   SKIP("Skip"),
   VANILLA("Vanilla"),
   PACKETLESS("Packetless");

   private String displayName;
   private static final PhaseMode[] VALUES = new PhaseMode[]{VERTICAL, FULLBLOCK, SKIP, VANILLA, PACKETLESS};

   PhaseMode(String string2) {
      this.displayName = string2;
   }

   public static String displayNameOf(PhaseMode phaseMode) {
      return phaseMode.displayName;
   }
}
