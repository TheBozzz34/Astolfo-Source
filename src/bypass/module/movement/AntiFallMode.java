package bypass.module.movement;

public enum AntiFallMode {
   SET_BACK("Set Back"),
   PACKET("Packet"),
   LAG("Lag");

   private String J;
   private static final AntiFallMode[] P = new AntiFallMode[]{SET_BACK, PACKET, LAG};

   AntiFallMode(String string2) {
      this.J = string2;
   }

   public static String M(AntiFallMode antiFallMode) {
      return antiFallMode.J;
   }
}
