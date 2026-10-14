package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;

public class IllegalAnglesCheck extends MovementCheck {
   public IllegalAnglesCheck() {
      super("Illegal Angles");
   }

   @Override
   public int D(double d, double d2, double d3, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      if (f == 0.0F && g == 0.0F) {
         return 0;
      }

      if (g > 90.0F || g < -90.0F) {
         this.x(entityPlayer.getName());
      }

      return this.T;
   }

   @Override
   public int p() {
      return 1;
   }
}
