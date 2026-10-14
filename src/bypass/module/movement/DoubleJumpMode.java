package bypass.module.movement;

public enum DoubleJumpMode {
   VANILLA("Vanilla"),
   NCP("NCP");

   private String u;
   private static final DoubleJumpMode[] v = new DoubleJumpMode[]{VANILLA, NCP};

   DoubleJumpMode(String string2) {
      this.u = string2;
   }

   public static String T(DoubleJumpMode doubleJumpMode) {
      return doubleJumpMode.u;
   }
}
