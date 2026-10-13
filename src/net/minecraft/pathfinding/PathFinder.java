package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.pathfinder.NodeProcessor;

public class PathFinder {
   private Path path = new Path();
   private PathPoint[] pathOptions;
   private NodeProcessor nodeProcessor;
   private static final long a = 60414542067083L;
   private static final long b = -5668600070367346656L;

   public PathFinder(NodeProcessor var1) {
      this.pathOptions = new PathPoint[(int)b];
      this.nodeProcessor = var1;
   }

   public PathEntity createEntityPathTo(IBlockAccess var1, Entity var2, Entity var3, float var4) {
      return this.createEntityPathTo(var1, var2, var3.posX, var3.getEntityBoundingBox().minY, var3.posZ, var4);
   }

   public PathEntity createEntityPathTo(IBlockAccess var1, Entity var2, BlockPos var3, float var4) {
      return this.createEntityPathTo(var1, var2, var3.getX() + 0.5F, var3.getY() + 0.5F, var3.getZ() + 0.5F, var4);
   }

   private PathEntity createEntityPathTo(IBlockAccess var1, Entity var2, double var3, double var5, double var7, float var9) {
      this.path.clearPath();
      this.nodeProcessor.initProcessor(var1, var2);
      PathPoint var10 = this.nodeProcessor.getPathPointTo(var2);
      PathPoint var11 = this.nodeProcessor.getPathPointToCoords(var2, var3, var5, var7);
      PathEntity var12 = this.addToPath(var2, var10, var11, var9);
      this.nodeProcessor.postProcess();
      return var12;
   }

   private PathEntity addToPath(Entity var1, PathPoint var2, PathPoint var3, float var4) {
      var2.totalPathDistance = 0.0F;
      var2.distanceToNext = var2.distanceToSquared(var3);
      var2.distanceToTarget = var2.distanceToNext;
      this.path.clearPath();
      this.path.addPoint(var2);
      PathPoint var8 = var2;

      while (!this.path.isPathEmpty()) {
         PathPoint var9 = this.path.dequeue();
         if (var9.equals(var3)) {
            return this.createEntityPath(var2, var3);
         }

         if (var9.distanceToSquared(var3) < var8.distanceToSquared(var3)) {
            var8 = var9;
         }

         var9.visited = true;
         int var10 = this.nodeProcessor.findPathOptions(this.pathOptions, var1, var9, var3, var4);

         for (int var11 = 0; var11 < var10; var11++) {
            PathPoint var12 = this.pathOptions[var11];
            float var13 = var9.totalPathDistance + var9.distanceToSquared(var12);
            if (var13 < var4 * 2.0F && (!var12.isAssigned() || var13 < var12.totalPathDistance)) {
               var12.previous = var9;
               var12.totalPathDistance = var13;
               var12.distanceToNext = var12.distanceToSquared(var3);
               if (var12.isAssigned()) {
                  this.path.changeDistance(var12, var12.totalPathDistance + var12.distanceToNext);
               } else {
                  var12.distanceToTarget = var12.totalPathDistance + var12.distanceToNext;
                  this.path.addPoint(var12);
               }
            }
         }
      }

      return var8 == var2 ? null : this.createEntityPath(var2, var8);
   }

   private PathEntity createEntityPath(PathPoint var1, PathPoint var2) {
      int var6 = 1;

      for (PathPoint var7 = var2; var7.previous != null; var7 = var7.previous) {
         var6++;
      }

      PathPoint[] var10 = new PathPoint[var6];
      PathPoint var8 = var2;

      for (var10[--var6] = var2; var8.previous != null; var10[--var6] = var8) {
         var8 = var8.previous;
      }

      return new PathEntity(var10);
   }
}
