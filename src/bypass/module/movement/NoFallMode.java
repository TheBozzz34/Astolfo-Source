package bypass.module.movement;

public enum NoFallMode {
   SPOOF("Spoof"),
   PACKET("Packet"),
   NO_GROUND("No Ground"),
   AAC_3315("AAC 3.3.15"),
   AAC_3311("AAC 3.3.11");

   private String U;
   private static final NoFallMode[] J = new NoFallMode[]{SPOOF, PACKET, NO_GROUND, AAC_3315, AAC_3311};

   NoFallMode(String string2) {
      this.U = string2;
   }

   public static String W(NoFallMode noFallMode) {
      return noFallMode.U;
   }
}
