package net.minecraft.pathfinding;

public class Path {
   private PathPoint[] pathPoints;
   private int count;
   private static final long a = 38522349457216L;
   private static final long c = -6084360137345072128L;

   public Path() {
      this.pathPoints = new PathPoint[(int)c];
   }

   public PathPoint addPoint(PathPoint var1) {
      if (var1.index >= 0) {
         throw new IllegalStateException("OW KNOWS!");
      }

      if (this.count == this.pathPoints.length) {
         PathPoint[] var5 = new PathPoint[this.count << 1];
         System.arraycopy(this.pathPoints, 0, var5, 0, this.count);
         this.pathPoints = var5;
      }

      this.pathPoints[this.count] = var1;
      var1.index = this.count;
      this.sortBack(this.count++);
      return var1;
   }

   public void clearPath() {
      this.count = 0;
   }

   public PathPoint dequeue() {
      PathPoint var4 = this.pathPoints[0];
      this.pathPoints[0] = this.pathPoints[--this.count];
      this.pathPoints[this.count] = null;
      if (this.count > 0) {
         this.sortForward(0);
      }

      var4.index = -1;
      return var4;
   }

   public void changeDistance(PathPoint var1, float var2) {
      float var6 = var1.distanceToTarget;
      var1.distanceToTarget = var2;
      if (var2 < var6) {
         this.sortBack(var1.index);
      } else {
         this.sortForward(var1.index);
      }
   }

   private void sortBack(int var1) {
      PathPoint var5 = this.pathPoints[var1];
      float var7 = var5.distanceToTarget;

      while (var1 > 0) {
         int var6 = var1 - 1 >> 1;
         PathPoint var8 = this.pathPoints[var6];
         if (var7 >= var8.distanceToTarget) {
            break;
         }

         this.pathPoints[var1] = var8;
         var8.index = var1;
         var1 = var6;
      }

      this.pathPoints[var1] = var5;
      var5.index = var1;
   }

   private void sortForward(int var1) {
      PathPoint var5 = this.pathPoints[var1];
      float var6 = var5.distanceToTarget;

      while (true) {
         int var7 = 1 + (var1 << 1);
         int var8 = var7 + 1;
         if (var7 >= this.count) {
            break;
         }

         PathPoint var9 = this.pathPoints[var7];
         float var10 = var9.distanceToTarget;
         PathPoint var11;
         float var12;
         if (var8 >= this.count) {
            var11 = null;
            var12 = Float.POSITIVE_INFINITY;
         } else {
            var11 = this.pathPoints[var8];
            var12 = var11.distanceToTarget;
         }

         if (var10 < var12) {
            if (var10 >= var6) {
               break;
            }

            this.pathPoints[var1] = var9;
            var9.index = var1;
            var1 = var7;
         } else {
            if (var12 >= var6) {
               break;
            }

            this.pathPoints[var1] = var11;
            var11.index = var1;
            var1 = var8;
         }
      }

      this.pathPoints[var1] = var5;
      var5.index = var1;
   }

   public boolean isPathEmpty() {
      return this.count == 0;
   }
}
