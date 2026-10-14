package bypass.module.movement;

public enum LongJumpMode {
   NORMAL("Normal"),
   SAFE("Safe"),
   WATCHDOG("Watchdog");

   private String D;
   private static final LongJumpMode[] O = new LongJumpMode[]{NORMAL, SAFE, WATCHDOG};

   LongJumpMode(String string2) {
      this.D = string2;
   }
}
