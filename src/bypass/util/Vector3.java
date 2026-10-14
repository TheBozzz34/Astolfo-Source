package bypass.util;

import net.minecraft.util.Vec3;

public class Vector3 {
   private double x;
   private double y;
   private double z;

   public Vector3(double d, double d2, double d3) {
      this.x = d;
      this.y = d2;
      this.z = d3;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public Vector3 add(double d, double d2, double d3) {
      return new Vector3(this.x + d, this.y + d2, this.z + d3);
   }

   public Vector3 floor() {
      return new Vector3(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
   }

   public double distanceSq(Vector3 vector3) {
      return Math.pow(vector3.x - this.x, 2.0) + Math.pow(vector3.y - this.y, 2.0) + Math.pow(vector3.z - this.z, 2.0);
   }

   public Vector3 add(Vector3 vector3) {
      return this.add(vector3.getX(), vector3.getY(), vector3.getZ());
   }

   public Vec3 toVec3() {
      return new Vec3(this.x, this.y, this.z);
   }

   @Override
   public String toString() {
      return "[" + this.x + ";" + this.y + ";" + this.z + "]";
   }
}
