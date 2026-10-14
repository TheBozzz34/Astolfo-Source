package bypass.module.player;

public enum StepMode {
   VANILLA("Vanilla"),
   NCP("NCP"),
   MOTION("Motion");

   private String O;
   private static final StepMode[] b = new StepMode[]{VANILLA, NCP, MOTION};

   StepMode(String string2) {
      this.O = string2;
   }

   public static String l(StepMode stepMode) {
      return stepMode.O;
   }
}
