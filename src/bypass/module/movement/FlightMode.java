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

   private String c;
   private static final FlightMode[] r = new FlightMode[]{VANILLA, GWEN, COLLISION, AAC_3, AAC_3312, AAC_3313, MC_CENTRAL, SENTINEL, FAITHFUL, CUSTOM};

   FlightMode(String string2) {
      this.c = string2;
   }

   public static String A(FlightMode flightMode) {
      return flightMode.c;
   }
}
