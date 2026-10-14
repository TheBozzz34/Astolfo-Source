package net.minecraft.world.pathfinder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class WalkNodeProcessor extends NodeProcessor {
   private boolean canEnterDoors;
   private boolean canBreakDoors;
   private boolean avoidsWater;
   private boolean canSwim;
   private boolean shouldAvoidWater;
   private static final long b = 40810530841628L;

   @Override
   public void initProcessor(IBlockAccess iblockaccessIn, Entity entityIn) {
      super.initProcessor(iblockaccessIn, entityIn);
      this.shouldAvoidWater = this.avoidsWater;
   }

   @Override
   public void postProcess() {
      super.postProcess();
      this.avoidsWater = this.shouldAvoidWater;
   }

   @Override
   public PathPoint getPathPointTo(Entity entityIn) {
      int i;
      if (this.canSwim && entityIn.isInWater()) {
         i = (int)entityIn.getEntityBoundingBox().minY;
         BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos(MathHelper.floor_double(entityIn.posX), i, MathHelper.floor_double(entityIn.posZ));

         for (Block block = this.blockaccess.getBlockState(mutableBlockPos).getBlock();
            block == Blocks.flowing_water || block == Blocks.water;
            block = this.blockaccess.getBlockState(mutableBlockPos).getBlock()
         ) {
            mutableBlockPos.set(MathHelper.floor_double(entityIn.posX), ++i, MathHelper.floor_double(entityIn.posZ));
         }

         this.avoidsWater = false;
      } else {
         i = MathHelper.floor_double(entityIn.getEntityBoundingBox().minY + 0.5);
      }

      return this.openPoint(MathHelper.floor_double(entityIn.getEntityBoundingBox().minX), i, MathHelper.floor_double(entityIn.getEntityBoundingBox().minZ));
   }

   @Override
   public PathPoint getPathPointToCoords(Entity entityIn, double x, double y, double target) {
      return this.openPoint(
         MathHelper.floor_double(x - entityIn.width / 2.0F), MathHelper.floor_double(y), MathHelper.floor_double(target - entityIn.width / 2.0F)
      );
   }

   @Override
   public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
      int i = 0;
      byte b = 0;
      if (this.getVerticalOffset(entityIn, currentPoint.xCoord, currentPoint.yCoord + 1, currentPoint.zCoord) == 1) {
         b = 1;
      }

      PathPoint pathPoint = this.getSafePoint(entityIn, currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord + 1, b);
      PathPoint pathPoint2 = this.getSafePoint(entityIn, currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord, b);
      PathPoint pathPoint3 = this.getSafePoint(entityIn, currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord, b);
      PathPoint pathPoint4 = this.getSafePoint(entityIn, currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord - 1, b);
      if (pathPoint != null && !pathPoint.visited && pathPoint.distanceTo(targetPoint) < maxDistance) {
         pathOptions[i++] = pathPoint;
      }

      if (pathPoint2 != null && !pathPoint2.visited && pathPoint2.distanceTo(targetPoint) < maxDistance) {
         pathOptions[i++] = pathPoint2;
      }

      if (pathPoint3 != null && !pathPoint3.visited && pathPoint3.distanceTo(targetPoint) < maxDistance) {
         pathOptions[i++] = pathPoint3;
      }

      if (pathPoint4 != null && !pathPoint4.visited && pathPoint4.distanceTo(targetPoint) < maxDistance) {
         pathOptions[i++] = pathPoint4;
      }

      return i;
   }

   private PathPoint getSafePoint(Entity entityIn, int x, int y, int z, int i) {
      PathPoint pathPoint = null;
      int j = this.getVerticalOffset(entityIn, x, y, z);
      if (j == 2) {
         return this.openPoint(x, y, z);
      }

      if (j == 1) {
         pathPoint = this.openPoint(x, y, z);
      }

      if (pathPoint == null && i > 0 && j != -3 && j != -4 && this.getVerticalOffset(entityIn, x, y + i, z) == 1) {
         pathPoint = this.openPoint(x, y + i, z);
         y += i;
      }

      if (pathPoint != null) {
         int k = 0;
         int l = 0;

         while (y > 0) {
            l = this.getVerticalOffset(entityIn, x, y - 1, z);
            if (this.avoidsWater && l == -1) {
               return null;
            }

            if (l != 1) {
               break;
            }

            if (k++ >= entityIn.getMaxFallHeight()) {
               return null;
            }

            if (--y <= 0) {
               return null;
            }

            pathPoint = this.openPoint(x, y, z);
         }

         if (l == -2) {
            return null;
         }
      }

      return pathPoint;
   }

   private int getVerticalOffset(Entity entityIn, int x, int y, int z) {
      return func_176170_a(
         this.blockaccess, entityIn, x, y, z, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.avoidsWater, this.canBreakDoors, this.canEnterDoors
      );
   }

   public static int func_176170_a(
      IBlockAccess blockaccessIn,
      Entity entityIn,
      int x,
      int y,
      int z,
      int sizeX,
      int sizeY,
      int sizeZ,
      boolean avoidWater,
      boolean breakDoors,
      boolean enterDoors
   ) {
      boolean bl = false;
      BlockPos blockPos = new BlockPos(entityIn);
      BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

      for (int i = x; i < x + sizeX; i++) {
         for (int j = y; j < y + sizeY; j++) {
            for (int k = z; k < z + sizeZ; k++) {
               mutableBlockPos.set(i, j, k);
               Block block = blockaccessIn.getBlockState(mutableBlockPos).getBlock();
               if (block.getMaterial() != Material.air) {
                  if (block == Blocks.trapdoor || block == Blocks.iron_trapdoor) {
                     bl = true;
                  } else if (block != Blocks.flowing_water && block != Blocks.water) {
                     if (!enterDoors && block instanceof BlockDoor && block.getMaterial() == Material.wood) {
                        return 0;
                     }
                  } else {
                     if (avoidWater) {
                        return -1;
                     }

                     bl = true;
                  }

                  if (entityIn.worldObj.getBlockState(mutableBlockPos).getBlock() instanceof BlockRailBase) {
                     if (!(entityIn.worldObj.getBlockState(blockPos).getBlock() instanceof BlockRailBase)
                        && !(entityIn.worldObj.getBlockState(blockPos.down()).getBlock() instanceof BlockRailBase)) {
                        return -3;
                     }
                  } else if (!block.isPassable(blockaccessIn, mutableBlockPos) && (!breakDoors || !(block instanceof BlockDoor) || block.getMaterial() != Material.wood)) {
                     if (block instanceof BlockFence || block instanceof BlockFenceGate || block instanceof BlockWall) {
                        return -3;
                     }

                     if (block == Blocks.trapdoor || block == Blocks.iron_trapdoor) {
                        return -4;
                     }

                     Material material = block.getMaterial();
                     if (material != Material.lava) {
                        return 0;
                     }

                     if (!entityIn.isInLava()) {
                        return -2;
                     }
                  }
               }
            }
         }
      }

      return bl ? 2 : 1;
   }

   public void setEnterDoors(boolean canEnterDoorsIn) {
      this.canEnterDoors = canEnterDoorsIn;
   }

   public void setBreakDoors(boolean canBreakDoorsIn) {
      this.canBreakDoors = canBreakDoorsIn;
   }

   public void setAvoidsWater(boolean avoidsWaterIn) {
      this.avoidsWater = avoidsWaterIn;
   }

   public void setCanSwim(boolean canSwimIn) {
      this.canSwim = canSwimIn;
   }

   public boolean getEnterDoors() {
      return this.canEnterDoors;
   }

   public boolean j() {
      return this.canSwim;
   }

   public boolean n() {
      return this.avoidsWater;
   }
}
