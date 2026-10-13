package bypass.module.movement;

public enum LongJumpMode {
   NORMAL("Normal"),
   SAFE("Safe"),
   WATCHDOG("Watchdog");

   private String D;
   private static final LongJumpMode[] O = new LongJumpMode[]{NORMAL, SAFE, WATCHDOG};

   LongJumpMode(String var3) {
      this.D = var3;
   }
}
