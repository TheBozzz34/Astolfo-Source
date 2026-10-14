package bypass.module.movement;

public enum SneakMode {
   WATCHDOG("Watchdog"),
   ALWAYS("Always");

   private String U;
   private static final SneakMode[] x = new SneakMode[]{WATCHDOG, ALWAYS};

   SneakMode(String string2) {
      this.U = string2;
   }

   public static String T(SneakMode sneakMode) {
      return sneakMode.U;
   }
}
