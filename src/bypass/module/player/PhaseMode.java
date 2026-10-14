package bypass.module.player;

public enum PhaseMode {
   VERTICAL("Vertical"),
   FULLBLOCK("Full Block"),
   SKIP("Skip"),
   VANILLA("Vanilla"),
   PACKETLESS("Packetless");

   private String F;
   private static final PhaseMode[] A = new PhaseMode[]{VERTICAL, FULLBLOCK, SKIP, VANILLA, PACKETLESS};

   PhaseMode(String string2) {
      this.F = string2;
   }

   public static String G(PhaseMode phaseMode) {
      return phaseMode.F;
   }
}
