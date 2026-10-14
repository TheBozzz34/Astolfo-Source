package bypass.loader.check;

import java.util.TimerTask;

public final class MeasureTimerTask extends TimerTask {
   private volatile MeasureRegistry t;
   private volatile Measure[] V;

   public MeasureTimerTask(MeasureRegistry measureRegistry) {
      this.t = measureRegistry;
      this.V = MeasureRegistry.MEASURE_MAP.get(4);
   }

   @Override
   public void run() {
      this.t.W(this.V);
   }
}
