package net.minecraft.src;

import net.minecraft.util.Vec3;

public class CustomColorFader {
   private Vec3 color = null;
   private long timeUpdate = System.currentTimeMillis();
   private static final long a = 11617812161920L;

   public Vec3 getColor(double x, double y, double z) {
      if (this.color == null) {
         this.color = new Vec3(x, y, z);
         return this.color;
      }

      long l = System.currentTimeMillis();
      long m = l - this.timeUpdate;
      if (m == 0L) {
         return this.color;
      }

      this.timeUpdate = l;
      if (Math.abs(x - this.color.xCoord) < 0.004 && Math.abs(y - this.color.yCoord) < 0.004 && Math.abs(z - this.color.zCoord) < 0.004) {
         return this.color;
      }

      double d = m * 0.001;
      d = Config.limit(d, 0.0, 1.0);
      double e = x - this.color.xCoord;
      double d2 = y - this.color.yCoord;
      double d3 = z - this.color.zCoord;
      double d4 = this.color.xCoord + e * d;
      double d5 = this.color.yCoord + d2 * d;
      double d6 = this.color.zCoord + d3 * d;
      this.color = new Vec3(d4, d5, d6);
      return this.color;
   }
}
