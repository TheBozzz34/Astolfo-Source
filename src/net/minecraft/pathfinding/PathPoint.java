package net.minecraft.pathfinding;

import net.minecraft.util.MathHelper;

public class PathPoint {
   public final int xCoord;
   public final int yCoord;
   public final int zCoord;
   private final int hash;
   public int index = -1;
   public float totalPathDistance;
   public float distanceToNext;
   public float distanceToTarget;
   public PathPoint previous;
   public boolean visited;

   public PathPoint(int x, int y, int z) {
      this.xCoord = x;
      this.yCoord = y;
      this.zCoord = z;
      this.hash = makeHash(x, y, z);
   }

   public static int makeHash(int x, int y, int z) {
      return y & 0xFF | (x & 32767) << 8 | (z & 32767) << 24 | (x < 0 ? Integer.MIN_VALUE : 0) | (z < 0 ? 32768 : 0);
   }

   public float distanceTo(PathPoint pathpointIn) {
      float f = pathpointIn.xCoord - this.xCoord;
      float g = pathpointIn.yCoord - this.yCoord;
      float h = pathpointIn.zCoord - this.zCoord;
      return MathHelper.sqrt_float(f * f + g * g + h * h);
   }

   public float distanceToSquared(PathPoint pathpointIn) {
      float f = pathpointIn.xCoord - this.xCoord;
      float g = pathpointIn.yCoord - this.yCoord;
      float h = pathpointIn.zCoord - this.zCoord;
      return f * f + g * g + h * h;
   }

   @Override
   public boolean equals(Object object) {
      if (!(object instanceof PathPoint)) {
         return false;
      }

      PathPoint pathPoint = (PathPoint)object;
      return this.hash == pathPoint.hash && this.xCoord == pathPoint.xCoord && this.yCoord == pathPoint.yCoord && this.zCoord == pathPoint.zCoord;
   }

   @Override
   public int hashCode() {
      return this.hash;
   }

   public boolean isAssigned() {
      return this.index >= 0;
   }

   @Override
   public String toString() {
      return this.xCoord + ", " + this.yCoord + ", " + this.zCoord;
   }
}
