package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class SprintACheck extends MovementCheck {
   private double lastX;
   private double lastZ;

   public SprintACheck() {
      super("Sprint A");
   }

   @Override
   public int handleMovement(double d, double d2, double d3, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      if (f == 0.0F && g == 0.0F) {
         return 0;
      }

      double e = d - this.lastX;
      double d4 = d3 - this.lastZ;
      if (entityPlayer.isSprinting() && (this.lastX != 0.0 || this.lastZ != 0.0)) {
         EnumFacing enumFacing = this.getFacingFromYaw(f);
         EnumFacing enumFacing2;
         if (e > 0.0) {
            enumFacing2 = EnumFacing.EAST;
            if (d4 < 0.0) {
               enumFacing2 = EnumFacing.NORTH;
            }
         } else {
            enumFacing2 = EnumFacing.WEST;
            if (d4 > 0.0) {
               enumFacing2 = EnumFacing.SOUTH;
            }
         }

         if (enumFacing.getOpposite() == enumFacing2) {
            this.violations++;
         }
      }

      this.lastX = d;
      this.lastZ = d3;
      return this.violations;
   }

   private EnumFacing getFacingFromYaw(float f) {
      return EnumFacing.getHorizontal(MathHelper.floor_double(f * 4.0F / 360.0F + 0.5) & 3);
   }

   @Override
   public int getMaxViolations() {
      return 10;
   }
}
