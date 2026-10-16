package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;

public class NoSlowdownCheck extends MovementCheck {
   private double lastX;
   private double lastZ;
   private boolean sprintedWhileUsing;

   public NoSlowdownCheck() {
      super("NoSlowdown");
   }

   @Override
   public int handleMovement(double d, double d2, double d3, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      if (entityPlayer.isUsingItem()) {
         if (this.lastX != 0.0 && this.lastZ != 0.0) {
            double e = Math.hypot(d - this.lastX, d3 - this.lastZ);
            if (entityPlayer.isSprinting() && e >= 0.25 && e < 3.0 && this.sprintedWhileUsing) {
               this.flag(entityPlayer.getName());
            }

            if (entityPlayer.isSprinting()) {
               this.sprintedWhileUsing = true;
            }
         }

         this.lastX = d;
         this.lastZ = d3;
      } else {
         this.sprintedWhileUsing = false;
         this.lastZ = 0.0;
         this.lastX = 0.0;
      }

      return this.violations;
   }

   @Override
   public int getMaxViolations() {
      return 7;
   }
}
