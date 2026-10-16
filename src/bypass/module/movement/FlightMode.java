package bypass.module.movement;

public enum FlightMode {
   VANILLA("Vanilla"),
   GWEN("GWEN"),
   COLLISION("Collision"),
   AAC_3("AAC 3"),
   AAC_3312("AAC 3.3.12"),
   AAC_3313("AAC 3.3.13"),
   MC_CENTRAL("MC Central"),
   SENTINEL("Sentinel"),
   FAITHFUL("Faithful"),
   CUSTOM("Custom");

   private String displayName;
   private static final FlightMode[] VALUES = new FlightMode[]{VANILLA, GWEN, COLLISION, AAC_3, AAC_3312, AAC_3313, MC_CENTRAL, SENTINEL, FAITHFUL, CUSTOM};

   FlightMode(String string2) {
      this.displayName = string2;
   }

   public static String displayNameOf(FlightMode flightMode) {
      return flightMode.displayName;
   }
}
