package bypass.module.player;

public enum StepMode {
   VANILLA("Vanilla"),
   NCP("NCP"),
   MOTION("Motion");

   private String displayName;
   private static final StepMode[] VALUES = new StepMode[]{VANILLA, NCP, MOTION};

   StepMode(String string2) {
      this.displayName = string2;
   }

   public static String displayNameOf(StepMode stepMode) {
      return stepMode.displayName;
   }
}
