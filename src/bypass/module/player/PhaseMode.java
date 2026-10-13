package bypass.module.player;

public enum PhaseMode {
   VERTICAL("Vertical"),
   FULLBLOCK("Full Block"),
   SKIP("Skip"),
   VANILLA("Vanilla"),
   PACKETLESS("Packetless");

   private String F;
   private static final PhaseMode[] A = new PhaseMode[]{VERTICAL, FULLBLOCK, SKIP, VANILLA, PACKETLESS};

   PhaseMode(String var3) {
      this.F = var3;
   }

   public static String G(PhaseMode var0) {
      return var0.F;
   }
}
