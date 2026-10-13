package bypass.module.movement;

public enum DoubleJumpMode {
   VANILLA("Vanilla"),
   NCP("NCP");

   private String u;
   private static final DoubleJumpMode[] v = new DoubleJumpMode[]{VANILLA, NCP};

   DoubleJumpMode(String var3) {
      this.u = var3;
   }

   public static String T(DoubleJumpMode var0) {
      return var0.u;
   }
}
