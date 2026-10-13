package bypass.loader.check;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public enum MeasureRegistry {
   INSTANCE;

   public static final Map<Integer, Measure[]> MEASURE_MAP = new HashMap<>();
   private boolean F = true;
   private final int C;
   private static final long a = 75820948980876L;
   private static final long e = -971070038083603152L;

   MeasureRegistry() {
      this.C = (int)e;
   }

   public void k() {
      Timer var4 = new Timer();
      var4.schedule(new MeasureTimerTask(this), 0L, 30000L);
   }

   public void N(int var1) throws MeasureException {
      this.W(MEASURE_MAP.get(var1));
   }

   public boolean W(Measure... var1) throws MeasureException {
      if (var1 == null) {
         return false;
      }

      boolean var5 = false;

      for (Measure var9 : var1) {
         var9.E();
      }

      if (var5) {
         throw new MeasureException("Failed measures during authorization check");
      }

      if (this.F) {
         this.F = false;
      }

      return !var5;
   }

   static {
      MEASURE_MAP.put(1, new Measure[0]);
   }
}
