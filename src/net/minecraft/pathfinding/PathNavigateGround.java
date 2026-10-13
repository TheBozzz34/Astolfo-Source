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

   public PathNavigateGround(EntityLiving var1, World var2) {
      super(var1, var2);
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
         int var4 = (int)this.theEntity.getEntityBoundingBox().minY;
         Block var5 = this.worldObj
            .getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), var4, MathHelper.floor_double(this.theEntity.posZ)))
            .getBlock();
         int var6 = 0;

         while (var5 == Blocks.flowing_water || var5 == Blocks.water) {
            var5 = this.worldObj
               .getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), ++var4, MathHelper.floor_double(this.theEntity.posZ)))
               .getBlock();
            if (++var6 > (int)i) {
               return (int)this.theEntity.getEntityBoundingBox().minY;
            }
         }

         return var4;
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

         for (int var4 = 0; var4 < this.currentPath.getCurrentPathLength(); var4++) {
            PathPoint var5 = this.currentPath.getPathPointFromIndex(var4);
            if (this.worldObj.canSeeSky(new BlockPos(var5.xCoord, var5.yCoord, var5.zCoord))) {
               this.currentPath.setCurrentPathLength(var4 - 1);
               return;
            }
         }
      }
   }

   @Override
   public boolean isDirectPathBetweenPoints(Vec3 var1, Vec3 var2, int var3, int var4, int var5) {
      int var9 = MathHelper.floor_double(var1.xCoord);
      int var10 = MathHelper.floor_double(var1.zCoord);
      double var11 = var2.xCoord - var1.xCoord;
      double var13 = var2.zCoord - var1.zCoord;
      double var15 = var11 * var11 + var13 * var13;
      if (var15 < 1.0E-8) {
         return false;
      }

      double var17 = 1.0 / Math.sqrt(var15);
      var11 *= var17;
      var13 *= var17;
      var3 += 2;
      var5 += 2;
      if (!this.isSafeToStandAt(var9, (int)var1.yCoord, var10, var3, var4, var5, var1, var11, var13)) {
         return false;
      }

      var3 -= 2;
      var5 -= 2;
      double var19 = 1.0 / Math.abs(var11);
      double var21 = 1.0 / Math.abs(var13);
      double var23 = var9 * 1 - var1.xCoord;
      double var25 = var10 * 1 - var1.zCoord;
      if (var11 >= 0.0) {
         var23++;
      }

      if (var13 >= 0.0) {
         var25++;
      }

      var23 /= var11;
      var25 /= var13;
      int var27 = var11 < 0.0 ? -1 : 1;
      int var28 = var13 < 0.0 ? -1 : 1;
      int var29 = MathHelper.floor_double(var2.xCoord);
      int var30 = MathHelper.floor_double(var2.zCoord);
      int var31 = var29 - var9;
      int var32 = var30 - var10;

      while (var31 * var27 > 0 || var32 * var28 > 0) {
         if (var23 < var25) {
            var23 += var19;
            var9 += var27;
            var31 = var29 - var9;
         } else {
            var25 += var21;
            var10 += var28;
            var32 = var30 - var10;
         }

         if (!this.isSafeToStandAt(var9, (int)var1.yCoord, var10, var3, var4, var5, var1, var11, var13)) {
            return false;
         }
      }

      return true;
   }

   private boolean isSafeToStandAt(int var1, int var2, int var3, int var4, int var5, int var6, Vec3 var7, double var8, double var10) {
      int var15 = var1 - var4 / 2;
      int var16 = var3 - var6 / 2;
      if (!this.isPositionClear(var15, var2, var16, var4, var5, var6, var7, var8, var10)) {
         return false;
      }

      for (int var17 = var15; var17 < var15 + var4; var17++) {
         for (int var18 = var16; var18 < var16 + var6; var18++) {
            double var19 = var17 + 0.5 - var7.xCoord;
            double var21 = var18 + 0.5 - var7.zCoord;
            if (var19 * var8 + var21 * var10 >= 0.0) {
               Block var23 = this.worldObj.getBlockState(new BlockPos(var17, var2 - 1, var18)).getBlock();
               Material var24 = var23.getMaterial();
               if (var24 == Material.air) {
                  return false;
               }

               if (var24 == Material.water && !this.theEntity.isInWater()) {
                  return false;
               }

               if (var24 == Material.lava) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private boolean isPositionClear(int var1, int var2, int var3, int var4, int var5, int var6, Vec3 var7, double var8, double var10) {
      for (BlockPos var16 : BlockPos.getAllInBox(new BlockPos(var1, var2, var3), new BlockPos(var1 + var4 - 1, var2 + var5 - 1, var3 + var6 - 1))) {
         double var17 = var16.getX() + 0.5 - var7.xCoord;
         double var19 = var16.getZ() + 0.5 - var7.zCoord;
         if (var17 * var8 + var19 * var10 >= 0.0) {
            Block var21 = this.worldObj.getBlockState(var16).getBlock();
            if (!var21.isPassable(this.worldObj, var16)) {
               return false;
            }
         }
      }

      return true;
   }

   public void setAvoidsWater(boolean var1) {
      this.nodeProcessor.setAvoidsWater(var1);
   }

   public boolean S() {
      return this.nodeProcessor.n();
   }

   public void setBreakDoors(boolean var1) {
      this.nodeProcessor.setBreakDoors(var1);
   }

   public void D(boolean var1) {
      this.nodeProcessor.setEnterDoors(var1);
   }

   public boolean getEnterDoors() {
      return this.nodeProcessor.getEnterDoors();
   }

   public void setCanSwim(boolean var1) {
      this.nodeProcessor.setCanSwim(var1);
   }

   public boolean j() {
      return this.nodeProcessor.j();
   }

   public void H(boolean var1) {
      this.shouldAvoidSun = var1;
   }
}
