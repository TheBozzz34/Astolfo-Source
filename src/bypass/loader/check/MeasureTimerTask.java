package bypass.loader.check;

import java.util.TimerTask;

public final class MeasureTimerTask extends TimerTask {
   private volatile MeasureRegistry t;
   private volatile Measure[] V;
   private static final long a = 15361104143142L;

   public MeasureTimerTask(MeasureRegistry var1) {
      this.t = var1;
      this.V = MeasureRegistry.MEASURE_MAP.get(4);
   }

   @Override
   public void run() {
      this.t.W(this.V);
   }
}
