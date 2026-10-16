package bypass.module.movement;

public enum JesusMode {
   SOLID("Solid"),
   FLOAT("Float"),
   DOLPHIN("Dolphin");

   private String displayName;
   private static final JesusMode[] VALUES = new JesusMode[]{SOLID, FLOAT, DOLPHIN};

   JesusMode(String string2) {
      this.displayName = string2;
   }

   public static String displayNameOf(JesusMode jesusMode) {
      return jesusMode.displayName;
   }
}
