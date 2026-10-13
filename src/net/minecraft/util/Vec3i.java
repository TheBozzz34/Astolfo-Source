package net.minecraft.util;

import com.google.common.base.Objects;

public class Vec3i implements Comparable<Vec3i> {
   public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);
   private final int x;
   private final int y;
   private final int z;
   private static final long d = 24061978726379L;

   public Vec3i(int var1, int var2, int var3) {
      this.x = var1;
      this.y = var2;
      this.z = var3;
   }

   public Vec3i(double var1, double var3, double var5) {
      this(MathHelper.floor_double(var1), MathHelper.floor_double(var3), MathHelper.floor_double(var5));
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof Vec3i)) {
         return false;
      }

      Vec3i var5 = (Vec3i)var1;
      return this.getX() != var5.getX() ? false : (this.getY() != var5.getY() ? false : this.getZ() == var5.getZ());
   }

   @Override
   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public int compareTo(Vec3i var1) {
      return this.getY() == var1.getY() ? (this.getZ() == var1.getZ() ? this.getX() - var1.getX() : this.getZ() - var1.getZ()) : this.getY() - var1.getY();
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public Vec3i crossProduct(Vec3i var1) {
      return new Vec3i(
         this.getY() * var1.getZ() - this.getZ() * var1.getY(),
         this.getZ() * var1.getX() - this.getX() * var1.getZ(),
         this.getX() * var1.getY() - this.getY() * var1.getX()
      );
   }

   public double distanceSq(double var1, double var3, double var5) {
      double var7 = this.getX() - var1;
      double var9 = this.getY() - var3;
      double var11 = this.getZ() - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double distanceSqToCenter(double var1, double var3, double var5) {
      double var7 = this.getX() + 0.5 - var1;
      double var9 = this.getY() + 0.5 - var3;
      double var11 = this.getZ() + 0.5 - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double distanceSq(Vec3i var1) {
      return this.distanceSq(var1.getX(), var1.getY(), var1.getZ());
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }
}
