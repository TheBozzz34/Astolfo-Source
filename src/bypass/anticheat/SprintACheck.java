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
   public int D(double var1, double var3, double var5, float var7, float var8, boolean var9, EntityPlayer var10) {
      if (var7 == 0.0F && var8 == 0.0F) {
         return 0;
      }

      double var14 = var1 - this.S;
      double var16 = var5 - this.y;
      if (var10.isSprinting() && (this.S != 0.0 || this.y != 0.0)) {
         EnumFacing var18 = this.Z(var7);
         EnumFacing var19;
         if (var14 > 0.0) {
            var19 = EnumFacing.EAST;
            if (var16 < 0.0) {
               var19 = EnumFacing.NORTH;
            }
         } else {
            var19 = EnumFacing.WEST;
            if (var16 > 0.0) {
               var19 = EnumFacing.SOUTH;
            }
         }

         if (var18.getOpposite() == var19) {
            this.T++;
         }
      }

      this.S = var1;
      this.y = var5;
      return this.T;
   }

   private EnumFacing Z(float var1) {
      return EnumFacing.getHorizontal(MathHelper.floor_double(var1 * 4.0F / 360.0F + 0.5) & 3);
   }

   @Override
   public int p() {
      return (int)e;
   }
}
