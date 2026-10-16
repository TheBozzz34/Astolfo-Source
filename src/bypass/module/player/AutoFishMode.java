package bypass.module.player;

public enum AutoFishMode {
   SOUND("Sound"),
   MOTION("Motion"),
   TEXT("Text");

   private String displayName;
   private static final AutoFishMode[] VALUES = new AutoFishMode[]{SOUND, MOTION, TEXT};

   AutoFishMode(String string2) {
      this.displayName = string2;
   }

   public static String displayNameOf(AutoFishMode autoFishMode) {
      return autoFishMode.displayName;
   }
}
