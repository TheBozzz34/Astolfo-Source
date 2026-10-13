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
   public int D(double var1, double var3, double var5, float var7, float var8, boolean var9, EntityPlayer var10) {
      double var15 = Math.hypot(var1 - this.J, var5 - this.M);
      double var17 = Math.abs(this.R - var3);
      if (var15 <= 0.4) {
         this.W.reset();
         this.V.clear();
      } else {
         double var19 = this.A(this.V);
         if (this.W.hasElapsed((float)(var19 + 500.0)) && var10.isSprinting() && ServerRotationTracker.g() < 1000L) {
            if (var15 > 5.0 && var15 < 50.0 && var17 < 5.0) {
               this.c(var10.getName(), (int)(var15 / 5.0));
               this.W.reset();
            }

            this.V.clear();
         }

         this.V.add((int)this.W.getElapsed());
      }

      this.W.reset();
      this.J = var1;
      this.M = var5;
      this.R = var3;
      return this.T;
   }

   private double A(List<Integer> var1) {
      Integer var5 = 0;
      if (var1.isEmpty()) {
         return var5.intValue();
      }

      for (Integer var7 : var1) {
         var5 = var5 + var7;
      }

      return var5.doubleValue() / var1.size();
   }

   @Override
   public int p() {
      return 10;
   }
}
