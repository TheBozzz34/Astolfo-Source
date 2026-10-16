package bypass.loader.check;

import java.util.TimerTask;

public final class MeasureTimerTask extends TimerTask {
   private volatile MeasureRegistry registry;
   private volatile Measure[] measures;

   public MeasureTimerTask(MeasureRegistry measureRegistry) {
      this.registry = measureRegistry;
      this.measures = MeasureRegistry.MEASURE_MAP.get(4);
   }

   @Override
   public void run() {
      this.registry.executeMeasures(this.measures);
   }
}
