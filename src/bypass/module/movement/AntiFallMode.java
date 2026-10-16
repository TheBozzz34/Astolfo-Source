package bypass.module.movement;

public enum AntiFallMode {
   SET_BACK("Set Back"),
   PACKET("Packet"),
   LAG("Lag");

   private String displayName;
   private static final AntiFallMode[] VALUES = new AntiFallMode[]{SET_BACK, PACKET, LAG};

   AntiFallMode(String string2) {
      this.displayName = string2;
   }

   public static String displayNameOf(AntiFallMode antiFallMode) {
      return antiFallMode.displayName;
   }
}
