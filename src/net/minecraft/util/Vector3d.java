package net.minecraft.util;

public class Vector3d {
   public double x;
   public double y;
   public double z;

   public Vector3d(double d, double d2, double d3) {
      this.x = d;
      this.y = d2;
      this.z = d3;
   }

   public Vector3d() {
      this.x = this.y = this.z = 0.0;
   }
}
