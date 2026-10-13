package net.minecraft.world.pathfinder;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class SwimNodeProcessor extends NodeProcessor {
   private static final long b = 76384019953179L;

   @Override
   public void initProcessor(IBlockAccess var1, Entity var2) {
      super.initProcessor(var1, var2);
   }

   @Override
   public void postProcess() {
      super.postProcess();
   }

   @Override
   public PathPoint getPathPointTo(Entity var1) {
      return this.openPoint(
         MathHelper.floor_double(var1.getEntityBoundingBox().minX),
         MathHelper.floor_double(var1.getEntityBoundingBox().minY + 0.5),
         MathHelper.floor_double(var1.getEntityBoundingBox().minZ)
      );
   }

   @Override
   public PathPoint getPathPointToCoords(Entity var1, double var2, double var4, double var6) {
      return this.openPoint(
         MathHelper.floor_double(var2 - var1.width / 2.0F), MathHelper.floor_double(var4 + 0.5), MathHelper.floor_double(var6 - var1.width / 2.0F)
      );
   }

   @Override
   public int findPathOptions(PathPoint[] var1, Entity var2, PathPoint var3, PathPoint var4, float var5) {
      int var9 = 0;

      for (EnumFacing var13 : EnumFacing.values()) {
         PathPoint var14 = this.getSafePoint(
            var2, var3.xCoord + var13.getFrontOffsetX(), var3.yCoord + var13.getFrontOffsetY(), var3.zCoord + var13.getFrontOffsetZ()
         );
         if (var14 != null && !var14.visited && var14.distanceTo(var4) < var5) {
            var1[var9++] = var14;
         }
      }

      return var9;
   }

   private PathPoint getSafePoint(Entity var1, int var2, int var3, int var4) {
      int var7 = this.func_176186_b(var1, var2, var3, var4);
      return var7 == -1 ? this.openPoint(var2, var3, var4) : null;
   }

   private int func_176186_b(Entity var1, int var2, int var3, int var4) {
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

      for (int var9 = var2; var9 < var2 + this.entitySizeX; var9++) {
         for (int var10 = var3; var10 < var3 + this.entitySizeY; var10++) {
            for (int var11 = var4; var11 < var4 + this.entitySizeZ; var11++) {
               Block var12 = this.blockaccess.getBlockState(var8.set(var9, var10, var11)).getBlock();
               if (var12.getMaterial() != Material.water) {
                  return 0;
               }
            }
         }
      }

      return -1;
   }
}
