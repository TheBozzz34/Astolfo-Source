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

   public PathFinder(NodeProcessor nodeProcessorIn) {
      this.pathOptions = new PathPoint[(int)b];
      this.nodeProcessor = nodeProcessorIn;
   }

   public PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity entityFrom, Entity entityTo, float dist) {
      return this.createEntityPathTo(blockaccess, entityFrom, entityTo.posX, entityTo.getEntityBoundingBox().minY, entityTo.posZ, dist);
   }

   public PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity entityIn, BlockPos targetPos, float dist) {
      return this.createEntityPathTo(blockaccess, entityIn, targetPos.getX() + 0.5F, targetPos.getY() + 0.5F, targetPos.getZ() + 0.5F, dist);
   }

   private PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity entityIn, double x, double y, double z, float distance) {
      this.path.clearPath();
      this.nodeProcessor.initProcessor(blockaccess, entityIn);
      PathPoint pathPoint = this.nodeProcessor.getPathPointTo(entityIn);
      PathPoint pathPoint2 = this.nodeProcessor.getPathPointToCoords(entityIn, x, y, z);
      PathEntity pathEntity = this.addToPath(entityIn, pathPoint, pathPoint2, distance);
      this.nodeProcessor.postProcess();
      return pathEntity;
   }

   private PathEntity addToPath(Entity entityIn, PathPoint pathpointStart, PathPoint pathpointEnd, float maxDistance) {
      pathpointStart.totalPathDistance = 0.0F;
      pathpointStart.distanceToNext = pathpointStart.distanceToSquared(pathpointEnd);
      pathpointStart.distanceToTarget = pathpointStart.distanceToNext;
      this.path.clearPath();
      this.path.addPoint(pathpointStart);
      PathPoint pathPoint = pathpointStart;

      while (!this.path.isPathEmpty()) {
         PathPoint pathPoint2 = this.path.dequeue();
         if (pathPoint2.equals(pathpointEnd)) {
            return this.createEntityPath(pathpointStart, pathpointEnd);
         }

         if (pathPoint2.distanceToSquared(pathpointEnd) < pathPoint.distanceToSquared(pathpointEnd)) {
            pathPoint = pathPoint2;
         }

         pathPoint2.visited = true;
         int i = this.nodeProcessor.findPathOptions(this.pathOptions, entityIn, pathPoint2, pathpointEnd, maxDistance);

         for (int j = 0; j < i; j++) {
            PathPoint pathPoint3 = this.pathOptions[j];
            float f = pathPoint2.totalPathDistance + pathPoint2.distanceToSquared(pathPoint3);
            if (f < maxDistance * 2.0F && (!pathPoint3.isAssigned() || f < pathPoint3.totalPathDistance)) {
               pathPoint3.previous = pathPoint2;
               pathPoint3.totalPathDistance = f;
               pathPoint3.distanceToNext = pathPoint3.distanceToSquared(pathpointEnd);
               if (pathPoint3.isAssigned()) {
                  this.path.changeDistance(pathPoint3, pathPoint3.totalPathDistance + pathPoint3.distanceToNext);
               } else {
                  pathPoint3.distanceToTarget = pathPoint3.totalPathDistance + pathPoint3.distanceToNext;
                  this.path.addPoint(pathPoint3);
               }
            }
         }
      }

      return pathPoint == pathpointStart ? null : this.createEntityPath(pathpointStart, pathPoint);
   }

   private PathEntity createEntityPath(PathPoint startIn, PathPoint end) {
      int i = 1;

      for (PathPoint pathPoint = end; pathPoint.previous != null; pathPoint = pathPoint.previous) {
         i++;
      }

      PathPoint[] pathPoints = new PathPoint[i];
      PathPoint pathPoint2 = end;

      for (pathPoints[--i] = end; pathPoint2.previous != null; pathPoints[--i] = pathPoint2) {
         pathPoint2 = pathPoint2.previous;
      }

      return new PathEntity(pathPoints);
   }
}
