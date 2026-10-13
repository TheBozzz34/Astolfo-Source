package bypass.module.movement;

public enum FlightCustomSpoofMode {
   NONE,
   ALWAYS,
   RANDOM;

   private static final FlightCustomSpoofMode[] b = new FlightCustomSpoofMode[]{NONE, ALWAYS, RANDOM};
}
