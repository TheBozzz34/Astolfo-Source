package bypass.module.movement;

public enum AntiFallMode {
   SET_BACK("Set Back"),
   PACKET("Packet"),
   LAG("Lag");

   private String J;
   private static final AntiFallMode[] P = new AntiFallMode[]{SET_BACK, PACKET, LAG};

   AntiFallMode(String var3) {
      this.J = var3;
   }

   public static String M(AntiFallMode var0) {
      return var0.J;
   }
}
