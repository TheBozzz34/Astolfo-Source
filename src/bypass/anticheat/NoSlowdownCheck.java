package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;

public class NoSlowdownCheck extends MovementCheck {
   private double N;
   private double X;
   private boolean J;

   public NoSlowdownCheck() {
      super("NoSlowdown");
   }

   @Override
   public int D(double d, double d2, double d3, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      if (entityPlayer.isUsingItem()) {
         if (this.N != 0.0 && this.X != 0.0) {
            double e = Math.hypot(d - this.N, d3 - this.X);
            if (entityPlayer.isSprinting() && e >= 0.25 && e < 3.0 && this.J) {
               this.x(entityPlayer.getName());
            }

            if (entityPlayer.isSprinting()) {
               this.J = true;
            }
         }

         this.N = d;
         this.X = d3;
      } else {
         this.J = false;
         this.X = 0.0;
         this.N = 0.0;
      }

      return this.T;
   }

   @Override
   public int p() {
      return 7;
   }
}
