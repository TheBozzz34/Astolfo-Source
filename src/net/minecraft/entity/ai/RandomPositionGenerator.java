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

   public static Vec3 findRandomTarget(EntityCreature entitycreatureIn, int xz, int y) {
      return findRandomTargetBlock(entitycreatureIn, xz, y, (Vec3)null);
   }

   public static Vec3 findRandomTargetBlockTowards(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3) {
      staticVector = targetVec3.subtract(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ);
      return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
   }

   public static Vec3 findRandomTargetBlockAwayFrom(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3) {
      staticVector = new Vec3(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ).subtract(targetVec3);
      return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
   }

   private static Vec3 findRandomTargetBlock(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3) {
      Random random = entitycreatureIn.getRNG();
      boolean bl = false;
      int i = 0;
      int j = 0;
      int k = 0;
      float f = -99999.0F;
      boolean bl2;
      if (entitycreatureIn.hasHome()) {
         double d = entitycreatureIn.getHomePosition()
               .distanceSq(
                  MathHelper.floor_double(entitycreatureIn.posX),
                  MathHelper.floor_double(entitycreatureIn.posY),
                  MathHelper.floor_double(entitycreatureIn.posZ)
               )
            + 4.0;
         double e = entitycreatureIn.getMaximumHomeDistance() + xz;
         bl2 = d < e * e;
      } else {
         bl2 = false;
      }

      for (int l = 0; l < (int)b; l++) {
         int m = random.nextInt(2 * xz + 1) - xz;
         int n = random.nextInt(2 * y + 1) - y;
         int i2 = random.nextInt(2 * xz + 1) - xz;
         if (targetVec3 == null || m * targetVec3.xCoord + i2 * targetVec3.zCoord >= 0.0) {
            if (entitycreatureIn.hasHome() && xz > 1) {
               BlockPos blockPos = entitycreatureIn.getHomePosition();
               if (entitycreatureIn.posX > blockPos.getX()) {
                  m -= random.nextInt(xz / 2);
               } else {
                  m += random.nextInt(xz / 2);
               }

               if (entitycreatureIn.posZ > blockPos.getZ()) {
                  i2 -= random.nextInt(xz / 2);
               } else {
                  i2 += random.nextInt(xz / 2);
               }
            }

            m += MathHelper.floor_double(entitycreatureIn.posX);
            n += MathHelper.floor_double(entitycreatureIn.posY);
            i2 += MathHelper.floor_double(entitycreatureIn.posZ);
            BlockPos blockPos2 = new BlockPos(m, n, i2);
            if (!bl2 || entitycreatureIn.isWithinHomeDistanceFromPosition(blockPos2)) {
               float g = entitycreatureIn.getBlockPathWeight(blockPos2);
               if (g > f) {
                  f = g;
                  i = m;
                  j = n;
                  k = i2;
                  bl = true;
               }
            }
         }
      }

      return bl ? new Vec3(i, j, k) : null;
   }
}
