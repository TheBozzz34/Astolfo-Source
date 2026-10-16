package bypass.loader.check;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public enum MeasureRegistry {
   INSTANCE;

   public static final Map<Integer, Measure[]> MEASURE_MAP = new HashMap<>();
   private boolean firstRun = true;
   private final int intervalMs = 30000;

   public void startTimer() {
      Timer timer = new Timer();
      timer.schedule(new MeasureTimerTask(this), 0L, 30000L);
   }

   public void runMeasures(int i) throws MeasureException {
      this.executeMeasures(MEASURE_MAP.get(i));
   }

   public boolean executeMeasures(Measure... measures) throws MeasureException {
      if (measures == null) {
         return false;
      }

      boolean bl = false;

      for (Measure measure : measures) {
         measure.execute();
      }

      if (bl) {
         throw new MeasureException("Failed measures during authorization check");
      }

      if (this.firstRun) {
         this.firstRun = false;
      }

      return !bl;
   }

   static {
      MEASURE_MAP.put(1, new Measure[0]);
   }
}
