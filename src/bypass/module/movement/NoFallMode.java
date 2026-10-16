package bypass.module.movement;

public enum NoFallMode {
   SPOOF("Spoof"),
   PACKET("Packet"),
   NO_GROUND("No Ground"),
   AAC_3315("AAC 3.3.15"),
   AAC_3311("AAC 3.3.11");

   private String displayName;
   private static final NoFallMode[] VALUES = new NoFallMode[]{SPOOF, PACKET, NO_GROUND, AAC_3315, AAC_3311};

   NoFallMode(String string2) {
      this.displayName = string2;
   }

   public static String displayNameOf(NoFallMode noFallMode) {
      return noFallMode.displayName;
   }
}
