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
   private static final long a = 77667759713957L;

   public PathPoint(int var1, int var2, int var3) {
      this.xCoord = var1;
      this.yCoord = var2;
      this.zCoord = var3;
      this.hash = makeHash(var1, var2, var3);
   }

   public static int makeHash(int var0, int var1, int var2) {
      return var1 & 0xFF | (var0 & 32767) << 8 | (var2 & 32767) << 24 | (var0 < 0 ? Integer.MIN_VALUE : 0) | (var2 < 0 ? 32768 : 0);
   }

   public float distanceTo(PathPoint var1) {
      float var2 = var1.xCoord - this.xCoord;
      float var3 = var1.yCoord - this.yCoord;
      float var4 = var1.zCoord - this.zCoord;
      return MathHelper.sqrt_float(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public float distanceToSquared(PathPoint var1) {
      float var2 = var1.xCoord - this.xCoord;
      float var3 = var1.yCoord - this.yCoord;
      float var4 = var1.zCoord - this.zCoord;
      return var2 * var2 + var3 * var3 + var4 * var4;
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof PathPoint)) {
         return false;
      }

      PathPoint var5 = (PathPoint)var1;
      return this.hash == var5.hash && this.xCoord == var5.xCoord && this.yCoord == var5.yCoord && this.zCoord == var5.zCoord;
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
