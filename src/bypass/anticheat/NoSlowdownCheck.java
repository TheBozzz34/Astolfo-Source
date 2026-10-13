package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;

public class NoSlowdownCheck extends MovementCheck {
   private double N;
   private double X;
   private boolean J;
   private static final long b = 98640950690292L;
   private static final long e = -5743630696602140665L;

   public NoSlowdownCheck() {
      super("NoSlowdown");
   }

   @Override
   public int D(double var1, double var3, double var5, float var7, float var8, boolean var9, EntityPlayer var10) {
      if (var10.isUsingItem()) {
         if (this.N != 0.0 && this.X != 0.0) {
            double var14 = Math.hypot(var1 - this.N, var5 - this.X);
            if (var10.isSprinting() && var14 >= 0.25 && var14 < 3.0 && this.J) {
               this.x(var10.getName());
            }

            if (var10.isSprinting()) {
               this.J = true;
            }
         }

         this.N = var1;
         this.X = var5;
      } else {
         this.J = false;
         this.X = 0.0;
         this.N = 0.0;
      }

      return this.T;
   }

   @Override
   public int p() {
      return (int)e;
   }
}
