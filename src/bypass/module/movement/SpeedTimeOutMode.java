package bypass.module.movement;

public enum SpeedTimeOutMode {
   NONE,
   DISABLE,
   WAIT;

   private static final SpeedTimeOutMode[] VALUES = new SpeedTimeOutMode[]{NONE, DISABLE, WAIT};
}
