package bypass.loader.check;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public enum MeasureRegistry {
   INSTANCE;

   public static final Map<Integer, Measure[]> MEASURE_MAP = new HashMap<>();
   private boolean F = true;
   private final int C = 30000;

   public void k() {
      Timer timer = new Timer();
      timer.schedule(new MeasureTimerTask(this), 0L, 30000L);
   }

   public void N(int i) throws MeasureException {
      this.W(MEASURE_MAP.get(i));
   }

   public boolean W(Measure... measures) throws MeasureException {
      if (measures == null) {
         return false;
      }

      boolean bl = false;

      for (Measure measure : measures) {
         measure.E();
      }

      if (bl) {
         throw new MeasureException("Failed measures during authorization check");
      }

      if (this.F) {
         this.F = false;
      }

      return !bl;
   }

   static {
      MEASURE_MAP.put(1, new Measure[0]);
   }
}
