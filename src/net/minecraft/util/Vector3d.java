package net.minecraft.util;

public class Vector3d {
   public double x;
   public double y;
   public double z;

   public Vector3d(double d, double e, double d2) {
      this.x = d;
      this.y = e;
      this.z = d2;
   }

   public Vector3d() {
      this.x = this.y = this.z = 0.0;
   }
}
