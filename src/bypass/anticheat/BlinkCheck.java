package bypass.anticheat;

import bypass.module.hidden.ServerRotationTracker;
import bypass.util.Stopwatch;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class BlinkCheck extends MovementCheck {
   private double J;
   private double M;
   private double R;
   private final Stopwatch W = new Stopwatch();
   private final List<Integer> V = new ArrayList<>(10);
   private static final long b = 41383641131652L;

   public BlinkCheck() {
      super("Blink");
   }

   @Override
   public int D(double d, double d2, double d3, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      double e = Math.hypot(d - this.J, d3 - this.M);
      double d4 = Math.abs(this.R - d2);
      if (e <= 0.4) {
         this.W.reset();
         this.V.clear();
      } else {
         double d5 = this.A(this.V);
         if (this.W.hasElapsed((float)(d5 + 500.0)) && entityPlayer.isSprinting() && ServerRotationTracker.g() < 1000L) {
            if (e > 5.0 && e < 50.0 && d4 < 5.0) {
               this.c(entityPlayer.getName(), (int)(e / 5.0));
               this.W.reset();
            }

            this.V.clear();
         }

         this.V.add((int)this.W.getElapsed());
      }

      this.W.reset();
      this.J = d;
      this.M = d3;
      this.R = d2;
      return this.T;
   }

   private double A(List<Integer> list) {
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
   public int p() {
      return 10;
   }
}
