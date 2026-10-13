package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class PathEntity {
   private final PathPoint[] points;
   private int currentPathIndex;
   private int pathLength;
   private static final long a = 55660991848995L;

   public PathEntity(PathPoint[] var1) {
      this.points = var1;
      this.pathLength = var1.length;
   }

   public void incrementPathIndex() {
      this.currentPathIndex++;
   }

   public boolean isFinished() {
      return this.currentPathIndex >= this.pathLength;
   }

   public PathPoint getFinalPathPoint() {
      return this.pathLength > 0 ? this.points[this.pathLength - 1] : null;
   }

   public PathPoint getPathPointFromIndex(int var1) {
      return this.points[var1];
   }

   public int getCurrentPathLength() {
      return this.pathLength;
   }

   public void setCurrentPathLength(int var1) {
      this.pathLength = var1;
   }

   public int getCurrentPathIndex() {
      return this.currentPathIndex;
   }

   public void setCurrentPathIndex(int var1) {
      this.currentPathIndex = var1;
   }

   public Vec3 getVectorFromIndex(Entity var1, int var2) {
      double var3 = this.points[var2].xCoord + (int)(var1.width + 1.0F) * 0.5;
      double var5 = this.points[var2].yCoord;
      double var7 = this.points[var2].zCoord + (int)(var1.width + 1.0F) * 0.5;
      return new Vec3(var3, var5, var7);
   }

   public Vec3 getPosition(Entity var1) {
      return this.getVectorFromIndex(var1, this.currentPathIndex);
   }

   public boolean isSamePath(PathEntity var1) {
      if (var1 == null) {
         return false;
      }

      if (var1.points.length != this.points.length) {
         return false;
      }

      for (int var5 = 0; var5 < this.points.length; var5++) {
         if (this.points[var5].xCoord != var1.points[var5].xCoord
            || this.points[var5].yCoord != var1.points[var5].yCoord
            || this.points[var5].zCoord != var1.points[var5].zCoord) {
            return false;
         }
      }

      return true;
   }

   public boolean isDestinationSame(Vec3 var1) {
      PathPoint var5 = this.getFinalPathPoint();
      return var5 == null ? false : var5.xCoord == (int)var1.xCoord && var5.zCoord == (int)var1.zCoord;
   }
}
