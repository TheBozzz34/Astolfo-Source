package net.minecraft.pathfinding;

public class Path {
   private PathPoint[] pathPoints = new PathPoint[1024];
   private int count;

   public PathPoint addPoint(PathPoint point) {
      if (point.index >= 0) {
         throw new IllegalStateException("OW KNOWS!");
      }

      if (this.count == this.pathPoints.length) {
         PathPoint[] pathPoints2 = new PathPoint[this.count << 1];
         System.arraycopy(this.pathPoints, 0, pathPoints2, 0, this.count);
         this.pathPoints = pathPoints2;
      }

      this.pathPoints[this.count] = point;
      point.index = this.count;
      this.sortBack(this.count++);
      return point;
   }

   public void clearPath() {
      this.count = 0;
   }

   public PathPoint dequeue() {
      PathPoint pathPoint = this.pathPoints[0];
      this.pathPoints[0] = this.pathPoints[--this.count];
      this.pathPoints[this.count] = null;
      if (this.count > 0) {
         this.sortForward(0);
      }

      pathPoint.index = -1;
      return pathPoint;
   }

   public void changeDistance(PathPoint pathPoint, float f) {
      float g = pathPoint.distanceToTarget;
      pathPoint.distanceToTarget = f;
      if (f < g) {
         this.sortBack(pathPoint.index);
      } else {
         this.sortForward(pathPoint.index);
      }
   }

   private void sortBack(int i) {
      PathPoint pathPoint = this.pathPoints[i];
      float f = pathPoint.distanceToTarget;

      while (i > 0) {
         int j = i - 1 >> 1;
         PathPoint pathPoint2 = this.pathPoints[j];
         if (f >= pathPoint2.distanceToTarget) {
            break;
         }

         this.pathPoints[i] = pathPoint2;
         pathPoint2.index = i;
         i = j;
      }

      this.pathPoints[i] = pathPoint;
      pathPoint.index = i;
   }

   private void sortForward(int i) {
      PathPoint pathPoint = this.pathPoints[i];
      float f = pathPoint.distanceToTarget;

      while (true) {
         int j = 1 + (i << 1);
         int k = j + 1;
         if (j >= this.count) {
            break;
         }

         PathPoint pathPoint2 = this.pathPoints[j];
         float g = pathPoint2.distanceToTarget;
         PathPoint pathPoint3;
         float h;
         if (k >= this.count) {
            pathPoint3 = null;
            h = Float.POSITIVE_INFINITY;
         } else {
            pathPoint3 = this.pathPoints[k];
            h = pathPoint3.distanceToTarget;
         }

         if (g < h) {
            if (g >= f) {
               break;
            }

            this.pathPoints[i] = pathPoint2;
            pathPoint2.index = i;
            i = j;
         } else {
            if (h >= f) {
               break;
            }

            this.pathPoints[i] = pathPoint3;
            pathPoint3.index = i;
            i = k;
         }
      }

      this.pathPoints[i] = pathPoint;
      pathPoint.index = i;
   }

   public boolean isPathEmpty() {
      return this.count == 0;
   }
}
