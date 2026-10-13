package net.minecraft.util;

public class Vector3d {
   public double x;
   public double y;
   public double z;

   public Vector3d(double var1, double var3, double var5) {
      this.x = var1;
      this.y = var3;
      this.z = var5;
   }

   public Vector3d() {
      this.x = this.y = this.z = 0.0;
   }
}
