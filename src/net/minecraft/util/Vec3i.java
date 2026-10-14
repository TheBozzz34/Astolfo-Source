package net.minecraft.util;

import com.google.common.base.Objects;

public class Vec3i implements Comparable<Vec3i> {
   public static final Vec3i NULL_VECTOR = new Vec3i(0, 0, 0);
   private final int x;
   private final int y;
   private final int z;

   public Vec3i(int xIn, int yIn, int zIn) {
      this.x = xIn;
      this.y = yIn;
      this.z = zIn;
   }

   public Vec3i(double xIn, double yIn, double zIn) {
      this(MathHelper.floor_double(xIn), MathHelper.floor_double(yIn), MathHelper.floor_double(zIn));
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof Vec3i)) {
         return false;
      }

      Vec3i vec3i = (Vec3i)object;
      return this.getX() != vec3i.getX() ? false : (this.getY() != vec3i.getY() ? false : this.getZ() == vec3i.getZ());
   }

   @Override
   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public int compareTo(Vec3i vec3i) {
      return this.getY() == vec3i.getY() ? (this.getZ() == vec3i.getZ() ? this.getX() - vec3i.getX() : this.getZ() - vec3i.getZ()) : this.getY() - vec3i.getY();
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

   public Vec3i crossProduct(Vec3i vec) {
      return new Vec3i(
         this.getY() * vec.getZ() - this.getZ() * vec.getY(),
         this.getZ() * vec.getX() - this.getX() * vec.getZ(),
         this.getX() * vec.getY() - this.getY() * vec.getX()
      );
   }

   public double distanceSq(double toX, double toY, double toZ) {
      double d = this.getX() - toX;
      double e = this.getY() - toY;
      double d2 = this.getZ() - toZ;
      return d * d + e * e + d2 * d2;
   }

   public double distanceSqToCenter(double xIn, double yIn, double zIn) {
      double d = this.getX() + 0.5 - xIn;
      double e = this.getY() + 0.5 - yIn;
      double d2 = this.getZ() + 0.5 - zIn;
      return d * d + e * e + d2 * d2;
   }

   public double distanceSq(Vec3i to) {
      return this.distanceSq(to.getX(), to.getY(), to.getZ());
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }
}
