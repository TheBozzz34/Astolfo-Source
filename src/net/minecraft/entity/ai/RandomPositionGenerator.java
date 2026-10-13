package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator {
   private static Vec3 staticVector = new Vec3(0.0, 0.0, 0.0);
   private static final long a = 120342492673288L;
   private static final long b = 7864873589072199690L;

   public static Vec3 findRandomTarget(EntityCreature var0, int var1, int var2) {
      return findRandomTargetBlock(var0, var1, var2, (Vec3)null);
   }

   public static Vec3 findRandomTargetBlockTowards(EntityCreature var0, int var1, int var2, Vec3 var3) {
      staticVector = var3.subtract(var0.posX, var0.posY, var0.posZ);
      return findRandomTargetBlock(var0, var1, var2, staticVector);
   }

   public static Vec3 findRandomTargetBlockAwayFrom(EntityCreature var0, int var1, int var2, Vec3 var3) {
      staticVector = new Vec3(var0.posX, var0.posY, var0.posZ).subtract(var3);
      return findRandomTargetBlock(var0, var1, var2, staticVector);
   }

   private static Vec3 findRandomTargetBlock(EntityCreature var0, int var1, int var2, Vec3 var3) {
      Random var7 = var0.getRNG();
      boolean var8 = false;
      int var9 = 0;
      int var10 = 0;
      int var11 = 0;
      float var12 = -99999.0F;
      boolean var13;
      if (var0.hasHome()) {
         double var14 = var0.getHomePosition()
               .distanceSq(MathHelper.floor_double(var0.posX), MathHelper.floor_double(var0.posY), MathHelper.floor_double(var0.posZ))
            + 4.0;
         double var16 = var0.getMaximumHomeDistance() + var1;
         var13 = var14 < var16 * var16;
      } else {
         var13 = false;
      }

      for (int var20 = 0; var20 < (int)b; var20++) {
         int var15 = var7.nextInt(2 * var1 + 1) - var1;
         int var22 = var7.nextInt(2 * var2 + 1) - var2;
         int var17 = var7.nextInt(2 * var1 + 1) - var1;
         if (var3 == null || var15 * var3.xCoord + var17 * var3.zCoord >= 0.0) {
            if (var0.hasHome() && var1 > 1) {
               BlockPos var18 = var0.getHomePosition();
               if (var0.posX > var18.getX()) {
                  var15 -= var7.nextInt(var1 / 2);
               } else {
                  var15 += var7.nextInt(var1 / 2);
               }

               if (var0.posZ > var18.getZ()) {
                  var17 -= var7.nextInt(var1 / 2);
               } else {
                  var17 += var7.nextInt(var1 / 2);
               }
            }

            var15 += MathHelper.floor_double(var0.posX);
            var22 += MathHelper.floor_double(var0.posY);
            var17 += MathHelper.floor_double(var0.posZ);
            BlockPos var25 = new BlockPos(var15, var22, var17);
            if (!var13 || var0.isWithinHomeDistanceFromPosition(var25)) {
               float var19 = var0.getBlockPathWeight(var25);
               if (var19 > var12) {
                  var12 = var19;
                  var9 = var15;
                  var10 = var22;
                  var11 = var17;
                  var8 = true;
               }
            }
         }
      }

      return var8 ? new Vec3(var9, var10, var11) : null;
   }
}
