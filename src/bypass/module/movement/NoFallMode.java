package bypass.module.movement;

public enum NoFallMode {
   SPOOF("Spoof"),
   PACKET("Packet"),
   NO_GROUND("No Ground"),
   AAC_3315("AAC 3.3.15"),
   AAC_3311("AAC 3.3.11");

   private String U;
   private static final NoFallMode[] J = new NoFallMode[]{SPOOF, PACKET, NO_GROUND, AAC_3315, AAC_3311};

   NoFallMode(String var3) {
      this.U = var3;
   }

   public static String W(NoFallMode var0) {
      return var0.U;
   }
}
