package bypass.module.other;

public enum WorldTimeMode {
   DAY(1000),
   NOON(6000),
   SUNSET(12000),
   NIGHT(13000),
   MIDNIGHT(18000),
   SUNRISE(23000),
   LOOP(0);

   private long T;
   private static final WorldTimeMode[] d = new WorldTimeMode[]{DAY, NOON, SUNSET, NIGHT, MIDNIGHT, SUNRISE, LOOP};

   WorldTimeMode(int var3) {
      this.T = var3;
   }

   public static long W(WorldTimeMode var0) {
      return var0.T;
   }
}
