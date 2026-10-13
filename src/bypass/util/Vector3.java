package bypass.util;

import net.minecraft.util.Vec3;

public class Vector3 {
   private double x;
   private double y;
   private double z;

   public Vector3(double var1, double var3, double var5) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
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

   public Vector3 add(double var1, double var3, double var5) {
      return new Vector3(this.x + var1, this.y + var3, this.z + var5);
   }

   public Vector3 floor() {
      return new Vector3(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
   }

   public double distanceSq(Vector3 var1) {
      return Math.pow(var1.x - this.x, 2.0) + Math.pow(var1.y - this.y, 2.0) + Math.pow(var1.z - this.z, 2.0);
   }

   public Vector3 add(Vector3 var1) {
      return this.add(var1.getX(), var1.getY(), var1.getZ());
   }

   public Vec3 toVec3() {
      return new Vec3(this.x, this.y, this.z);
   }

   @Override
   public String toString() {
      return "[" + this.x + ";" + this.y + ";" + this.z + "]";
   }
}
