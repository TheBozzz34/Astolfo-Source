package bypass.anticheat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class SprintACheck extends MovementCheck {
   private double S;
   private double y;
   private static final long b = 47639754789215L;
   private static final long e = -2692669612390088694L;

   public SprintACheck() {
      super("Sprint A");
   }

   @Override
   public int D(double d, double d2, double d3, float f, float g, boolean bl, EntityPlayer entityPlayer) {
      if (f == 0.0F && g == 0.0F) {
         return 0;
      }

      double e = d - this.S;
      double d4 = d3 - this.y;
      if (entityPlayer.isSprinting() && (this.S != 0.0 || this.y != 0.0)) {
         EnumFacing enumFacing = this.Z(f);
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
            this.T++;
         }
      }

      this.S = d;
      this.y = d3;
      return this.T;
   }

   private EnumFacing Z(float f) {
      return EnumFacing.getHorizontal(MathHelper.floor_double(f * 4.0F / 360.0F + 0.5) & 3);
   }

   @Override
   public int p() {
      return (int)e;
   }
}
