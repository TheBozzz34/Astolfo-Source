package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class PathNavigateGround extends PathNavigate {
   public WalkNodeProcessor nodeProcessor;
   private boolean shouldAvoidSun;
   private static final long c = 126185313564803L;
   private static final long i = -1421992564227047408L;

   public PathNavigateGround(EntityLiving entitylivingIn, World worldIn) {
      super(entitylivingIn, worldIn);
   }

   @Override
   public PathFinder getPathFinder() {
      this.nodeProcessor = new WalkNodeProcessor();
      this.nodeProcessor.setEnterDoors(true);
      return new PathFinder(this.nodeProcessor);
   }

   @Override
   public boolean canNavigate() {
      return this.theEntity.onGround
         || this.j() && this.isInLiquid()
         || this.theEntity.E() && this.theEntity instanceof EntityZombie && this.theEntity.ridingEntity instanceof EntityChicken;
   }

   @Override
   public Vec3 getEntityPosition() {
      return new Vec3(this.theEntity.posX, this.getPathablePosY(), this.theEntity.posZ);
   }

   private int getPathablePosY() {
      if (this.theEntity.isInWater() && this.j()) {
         int k = (int)this.theEntity.getEntityBoundingBox().minY;
         Block block = this.worldObj
            .getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), k, MathHelper.floor_double(this.theEntity.posZ)))
            .getBlock();
         int l = 0;

         while (block == Blocks.flowing_water || block == Blocks.water) {
            block = this.worldObj
               .getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), ++k, MathHelper.floor_double(this.theEntity.posZ)))
               .getBlock();
            if (++l > (int)i) {
               return (int)this.theEntity.getEntityBoundingBox().minY;
            }
         }

         return k;
      } else {
         return (int)(this.theEntity.getEntityBoundingBox().minY + 0.5);
      }
   }

   @Override
   public void removeSunnyPath() {
      super.removeSunnyPath();
      if (this.shouldAvoidSun) {
         if (this.worldObj
            .canSeeSky(
               new BlockPos(
                  MathHelper.floor_double(this.theEntity.posX),
                  (int)(this.theEntity.getEntityBoundingBox().minY + 0.5),
                  MathHelper.floor_double(this.theEntity.posZ)
               )
            )) {
            return;
         }

         for (int i = 0; i < this.currentPath.getCurrentPathLength(); i++) {
            PathPoint pathPoint = this.currentPath.getPathPointFromIndex(i);
            if (this.worldObj.canSeeSky(new BlockPos(pathPoint.xCoord, pathPoint.yCoord, pathPoint.zCoord))) {
               this.currentPath.setCurrentPathLength(i - 1);
               return;
            }
         }
      }
   }

   @Override
   public boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
      int i = MathHelper.floor_double(posVec31.xCoord);
      int j = MathHelper.floor_double(posVec31.zCoord);
      double d = posVec32.xCoord - posVec31.xCoord;
      double e = posVec32.zCoord - posVec31.zCoord;
      double d2 = d * d + e * e;
      if (d2 < 1.0E-8) {
         return false;
      }

      double d3 = 1.0 / Math.sqrt(d2);
      d *= d3;
      e *= d3;
      sizeX += 2;
      sizeZ += 2;
      if (!this.isSafeToStandAt(i, (int)posVec31.yCoord, j, sizeX, sizeY, sizeZ, posVec31, d, e)) {
         return false;
      }

      sizeX -= 2;
      sizeZ -= 2;
      double d4 = 1.0 / Math.abs(d);
      double d5 = 1.0 / Math.abs(e);
      double d6 = i * 1 - posVec31.xCoord;
      double d7 = j * 1 - posVec31.zCoord;
      if (d >= 0.0) {
         d6++;
      }

      if (e >= 0.0) {
         d7++;
      }

      d6 /= d;
      d7 /= e;
      int k = d < 0.0 ? -1 : 1;
      int l = e < 0.0 ? -1 : 1;
      int m = MathHelper.floor_double(posVec32.xCoord);
      int n = MathHelper.floor_double(posVec32.zCoord);
      int i2 = m - i;
      int i3 = n - j;

      while (i2 * k > 0 || i3 * l > 0) {
         if (d6 < d7) {
            d6 += d4;
            i += k;
            i2 = m - i;
         } else {
            d7 += d5;
            j += l;
            i3 = n - j;
         }

         if (!this.isSafeToStandAt(i, (int)posVec31.yCoord, j, sizeX, sizeY, sizeZ, posVec31, d, e)) {
            return false;
         }
      }

      return true;
   }

   private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3 vec31, double d, double d2) {
      int i = x - sizeX / 2;
      int j = z - sizeZ / 2;
      if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, d, d2)) {
         return false;
      }

      for (int k = i; k < i + sizeX; k++) {
         for (int l = j; l < j + sizeZ; l++) {
            double e = k + 0.5 - vec31.xCoord;
            double d3 = l + 0.5 - vec31.zCoord;
            if (e * d + d3 * d2 >= 0.0) {
               Block block = this.worldObj.getBlockState(new BlockPos(k, y - 1, l)).getBlock();
               Material material = block.getMaterial();
               if (material == Material.air) {
                  return false;
               }

               if (material == Material.water && !this.theEntity.isInWater()) {
                  return false;
               }

               if (material == Material.lava) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private boolean isPositionClear(int j, int k, int l, int m, int n, int i2, Vec3 vec3, double d, double d2) {
      for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(j, k, l), new BlockPos(j + m - 1, k + n - 1, l + i2 - 1))) {
         double e = blockPos.getX() + 0.5 - vec3.xCoord;
         double d3 = blockPos.getZ() + 0.5 - vec3.zCoord;
         if (e * d + d3 * d2 >= 0.0) {
            Block block = this.worldObj.getBlockState(blockPos).getBlock();
            if (!block.isPassable(this.worldObj, blockPos)) {
               return false;
            }
         }
      }

      return true;
   }

   public void setAvoidsWater(boolean avoidsWater) {
      this.nodeProcessor.setAvoidsWater(avoidsWater);
   }

   public boolean S() {
      return this.nodeProcessor.n();
   }

   public void setBreakDoors(boolean canBreakDoors) {
      this.nodeProcessor.setBreakDoors(canBreakDoors);
   }

   public void D(boolean bl) {
      this.nodeProcessor.setEnterDoors(bl);
   }

   public boolean getEnterDoors() {
      return this.nodeProcessor.getEnterDoors();
   }

   public void setCanSwim(boolean canSwim) {
      this.nodeProcessor.setCanSwim(canSwim);
   }

   public boolean j() {
      return this.nodeProcessor.j();
   }

   public void H(boolean bl) {
      this.shouldAvoidSun = bl;
   }
}
