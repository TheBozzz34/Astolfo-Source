package bypass.anticheat;

import bypass.module.hidden.ServerRotationTracker;
import bypass.util.Stopwatch;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class BlinkCheck extends MovementCheck {
   private double lastX;
   private double lastZ;
   private double lastY;
   private final Stopwatch moveTimer = new Stopwatch();
   private final List<Integer> moveIntervals = new ArrayList<>(10);

   public BlinkCheck() {
      super("Blink");
   }

   @Override
   public int handleMovement(double d, double e, double d2, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      double d3 = Math.hypot(d - this.lastX, d2 - this.lastZ);
      double d4 = Math.abs(this.lastY - e);
      if (d3 <= 0.4) {
         this.moveTimer.reset();
         this.moveIntervals.clear();
      } else {
         double d5 = this.getAverage(this.moveIntervals);
         if (this.moveTimer.hasElapsed((float)(d5 + 500.0)) && entityPlayer.isSprinting() && ServerRotationTracker.getTimeSinceTimeUpdate() < 1000L) {
            if (d3 > 5.0 && d3 < 50.0 && d4 < 5.0) {
               this.addViolations(entityPlayer.getName(), (int)(d3 / 5.0));
               this.moveTimer.reset();
            }

            this.moveIntervals.clear();
         }

         this.moveIntervals.add((int)this.moveTimer.getElapsed());
      }

      this.moveTimer.reset();
      this.lastX = d;
      this.lastZ = d2;
      this.lastY = e;
      return this.violations;
   }

   private double getAverage(List<Integer> list) {
      Integer integer = 0;
      if (list.isEmpty()) {
         return integer.intValue();
      }

      for (Integer integer2 : list) {
         integer = integer + integer2;
      }

      return integer.doubleValue() / list.size();
   }

   @Override
   public int getMaxViolations() {
      return 10;
   }
}
