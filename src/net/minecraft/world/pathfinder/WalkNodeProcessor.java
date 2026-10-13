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
   public void initProcessor(IBlockAccess var1, Entity var2) {
      super.initProcessor(var1, var2);
      this.shouldAvoidWater = this.avoidsWater;
   }

   @Override
   public void postProcess() {
      super.postProcess();
      this.avoidsWater = this.shouldAvoidWater;
   }

   @Override
   public PathPoint getPathPointTo(Entity var1) {
      int var5;
      if (this.canSwim && var1.isInWater()) {
         var5 = (int)var1.getEntityBoundingBox().minY;
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos(MathHelper.floor_double(var1.posX), var5, MathHelper.floor_double(var1.posZ));

         for (Block var7 = this.blockaccess.getBlockState(var6).getBlock();
            var7 == Blocks.flowing_water || var7 == Blocks.water;
            var7 = this.blockaccess.getBlockState(var6).getBlock()
         ) {
            var6.set(MathHelper.floor_double(var1.posX), ++var5, MathHelper.floor_double(var1.posZ));
         }

         this.avoidsWater = false;
      } else {
         var5 = MathHelper.floor_double(var1.getEntityBoundingBox().minY + 0.5);
      }

      return this.openPoint(MathHelper.floor_double(var1.getEntityBoundingBox().minX), var5, MathHelper.floor_double(var1.getEntityBoundingBox().minZ));
   }

   @Override
   public PathPoint getPathPointToCoords(Entity var1, double var2, double var4, double var6) {
      return this.openPoint(MathHelper.floor_double(var2 - var1.width / 2.0F), MathHelper.floor_double(var4), MathHelper.floor_double(var6 - var1.width / 2.0F));
   }

   @Override
   public int findPathOptions(PathPoint[] var1, Entity var2, PathPoint var3, PathPoint var4, float var5) {
      int var10 = 0;
      byte var11 = 0;
      if (this.getVerticalOffset(var2, var3.xCoord, var3.yCoord + 1, var3.zCoord) == 1) {
         var11 = 1;
      }

      PathPoint var12 = this.getSafePoint(var2, var3.xCoord, var3.yCoord, var3.zCoord + 1, var11);
      PathPoint var13 = this.getSafePoint(var2, var3.xCoord - 1, var3.yCoord, var3.zCoord, var11);
      PathPoint var14 = this.getSafePoint(var2, var3.xCoord + 1, var3.yCoord, var3.zCoord, var11);
      PathPoint var15 = this.getSafePoint(var2, var3.xCoord, var3.yCoord, var3.zCoord - 1, var11);
      if (var12 != null && !var12.visited && var12.distanceTo(var4) < var5) {
         var1[var10++] = var12;
      }

      if (var13 != null && !var13.visited && var13.distanceTo(var4) < var5) {
         var1[var10++] = var13;
      }

      if (var14 != null && !var14.visited && var14.distanceTo(var4) < var5) {
         var1[var10++] = var14;
      }

      if (var15 != null && !var15.visited && var15.distanceTo(var4) < var5) {
         var1[var10++] = var15;
      }

      return var10;
   }

   private PathPoint getSafePoint(Entity var1, int var2, int var3, int var4, int var5) {
      PathPoint var9 = null;
      int var10 = this.getVerticalOffset(var1, var2, var3, var4);
      if (var10 == 2) {
         return this.openPoint(var2, var3, var4);
      }

      if (var10 == 1) {
         var9 = this.openPoint(var2, var3, var4);
      }

      if (var9 == null && var5 > 0 && var10 != -3 && var10 != -4 && this.getVerticalOffset(var1, var2, var3 + var5, var4) == 1) {
         var9 = this.openPoint(var2, var3 + var5, var4);
         var3 += var5;
      }

      if (var9 != null) {
         int var11 = 0;
         int var12 = 0;

         while (var3 > 0) {
            var12 = this.getVerticalOffset(var1, var2, var3 - 1, var4);
            if (this.avoidsWater && var12 == -1) {
               return null;
            }

            if (var12 != 1) {
               break;
            }

            if (var11++ >= var1.getMaxFallHeight()) {
               return null;
            }

            if (--var3 <= 0) {
               return null;
            }

            var9 = this.openPoint(var2, var3, var4);
         }

         if (var12 == -2) {
            return null;
         }
      }

      return var9;
   }

   private int getVerticalOffset(Entity var1, int var2, int var3, int var4) {
      return func_176170_a(
         this.blockaccess,
         var1,
         var2,
         var3,
         var4,
         this.entitySizeX,
         this.entitySizeY,
         this.entitySizeZ,
         this.avoidsWater,
         this.canBreakDoors,
         this.canEnterDoors
      );
   }

   public static int func_176170_a(
      IBlockAccess var0, Entity var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, boolean var9, boolean var10
   ) {
      boolean var14 = false;
      BlockPos var15 = new BlockPos(var1);
      BlockPos.MutableBlockPos var16 = new BlockPos.MutableBlockPos();

      for (int var17 = var2; var17 < var2 + var5; var17++) {
         for (int var18 = var3; var18 < var3 + var6; var18++) {
            for (int var19 = var4; var19 < var4 + var7; var19++) {
               var16.set(var17, var18, var19);
               Block var20 = var0.getBlockState(var16).getBlock();
               if (var20.getMaterial() != Material.air) {
                  if (var20 == Blocks.trapdoor || var20 == Blocks.iron_trapdoor) {
                     var14 = true;
                  } else if (var20 != Blocks.flowing_water && var20 != Blocks.water) {
                     if (!var10 && var20 instanceof BlockDoor && var20.getMaterial() == Material.wood) {
                        return 0;
                     }
                  } else {
                     if (var8) {
                        return -1;
                     }

                     var14 = true;
                  }

                  if (var1.worldObj.getBlockState(var16).getBlock() instanceof BlockRailBase) {
                     if (!(var1.worldObj.getBlockState(var15).getBlock() instanceof BlockRailBase)
                        && !(var1.worldObj.getBlockState(var15.down()).getBlock() instanceof BlockRailBase)) {
                        return -3;
                     }
                  } else if (!var20.isPassable(var0, var16) && (!var9 || !(var20 instanceof BlockDoor) || var20.getMaterial() != Material.wood)) {
                     if (var20 instanceof BlockFence || var20 instanceof BlockFenceGate || var20 instanceof BlockWall) {
                        return -3;
                     }

                     if (var20 == Blocks.trapdoor || var20 == Blocks.iron_trapdoor) {
                        return -4;
                     }

                     Material var21 = var20.getMaterial();
                     if (var21 != Material.lava) {
                        return 0;
                     }

                     if (!var1.isInLava()) {
                        return -2;
                     }
                  }
               }
            }
         }
      }

      return var14 ? 2 : 1;
   }

   public void setEnterDoors(boolean var1) {
      this.canEnterDoors = var1;
   }

   public void setBreakDoors(boolean var1) {
      this.canBreakDoors = var1;
   }

   public void setAvoidsWater(boolean var1) {
      this.avoidsWater = var1;
   }

   public void setCanSwim(boolean var1) {
      this.canSwim = var1;
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
