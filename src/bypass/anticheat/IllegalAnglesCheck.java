package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;

public class IllegalAnglesCheck extends MovementCheck {
   private static final long b = 111621698677684L;

   public IllegalAnglesCheck() {
      super("Illegal Angles");
   }

   @Override
   public int D(double var1, double var3, double var5, float var7, float var8, boolean var9, EntityPlayer var10) {
      if (var7 == 0.0F && var8 == 0.0F) {
         return 0;
      }

      if (var8 > 90.0F || var8 < -90.0F) {
         this.x(var10.getName());
      }

      return this.T;
   }

   @Override
   public int p() {
      return 1;
   }
}
