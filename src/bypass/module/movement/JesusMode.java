package bypass.module.movement;

public enum JesusMode {
   SOLID("Solid"),
   FLOAT("Float"),
   DOLPHIN("Dolphin");

   private String g;
   private static final JesusMode[] C = new JesusMode[]{SOLID, FLOAT, DOLPHIN};

   JesusMode(String string2) {
      this.g = string2;
   }

   public static String R(JesusMode jesusMode) {
      return jesusMode.g;
   }
}
