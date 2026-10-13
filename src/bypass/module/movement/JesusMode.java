package bypass.module.movement;

public enum JesusMode {
   SOLID("Solid"),
   FLOAT("Float"),
   DOLPHIN("Dolphin");

   private String g;
   private static final JesusMode[] C = new JesusMode[]{SOLID, FLOAT, DOLPHIN};

   JesusMode(String var3) {
      this.g = var3;
   }

   public static String R(JesusMode var0) {
      return var0.g;
   }
}
