package bypass.module.player;

public enum AutoFishMode {
   SOUND("Sound"),
   MOTION("Motion"),
   TEXT("Text");

   private String i;
   private static final AutoFishMode[] M = new AutoFishMode[]{SOUND, MOTION, TEXT};

   AutoFishMode(String string2) {
      this.i = string2;
   }

   public static String m(AutoFishMode autoFishMode) {
      return autoFishMode.i;
   }
}
