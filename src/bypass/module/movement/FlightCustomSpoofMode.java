package bypass.module.movement;

public enum FlightCustomSpoofMode {
   NONE,
   ALWAYS,
   RANDOM;

   private static final FlightCustomSpoofMode[] VALUES = new FlightCustomSpoofMode[]{NONE, ALWAYS, RANDOM};
}
