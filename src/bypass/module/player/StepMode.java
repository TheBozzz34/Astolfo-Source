package bypass.module.player;

public enum StepMode {
   VANILLA("Vanilla"),
   NCP("NCP"),
   MOTION("Motion");

   private String O;
   private static final StepMode[] b = new StepMode[]{VANILLA, NCP, MOTION};

   StepMode(String var3) {
      this.O = var3;
   }

   public static String l(StepMode var0) {
      return var0.O;
   }
}
