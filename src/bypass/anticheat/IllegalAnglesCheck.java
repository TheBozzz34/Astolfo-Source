package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;

public class IllegalAnglesCheck extends MovementCheck {
   public IllegalAnglesCheck() {
      super("Illegal Angles");
   }

   @Override
   public int handleMovement(double d, double e, double d2, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      if (f == 0.0F && g == 0.0F) {
         return 0;
      }

      if (g > 90.0F || g < -90.0F) {
         this.flag(entityPlayer.getName());
      }

      return this.violations;
   }

   @Override
   public int getMaxViolations() {
      return 1;
   }
}
