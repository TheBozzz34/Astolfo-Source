package bypass.module.player;

public enum AutoFishMode {
   SOUND("Sound"),
   MOTION("Motion"),
   TEXT("Text");

   private String i;
   private static final AutoFishMode[] M = new AutoFishMode[]{SOUND, MOTION, TEXT};

   AutoFishMode(String var3) {
      this.i = var3;
   }

   public static String m(AutoFishMode var0) {
      return var0.i;
   }
}
