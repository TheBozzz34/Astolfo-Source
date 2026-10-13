package net.minecraft.src;

import net.minecraft.util.Vec3;

public class CustomColorFader {
   private Vec3 color = null;
   private long timeUpdate = System.currentTimeMillis();
   private static final long a = 11617812161920L;

   public Vec3 getColor(double var1, double var3, double var5) {
      if (this.color == null) {
         this.color = new Vec3(var1, var3, var5);
         return this.color;
      }

      long var10 = System.currentTimeMillis();
      long var12 = var10 - this.timeUpdate;
      if (var12 == 0L) {
         return this.color;
      }

      this.timeUpdate = var10;
      if (Math.abs(var1 - this.color.xCoord) < 0.004 && Math.abs(var3 - this.color.yCoord) < 0.004 && Math.abs(var5 - this.color.zCoord) < 0.004) {
         return this.color;
      }

      double var14 = var12 * 0.001;
      var14 = Config.limit(var14, 0.0, 1.0);
      double var16 = var1 - this.color.xCoord;
      double var18 = var3 - this.color.yCoord;
      double var20 = var5 - this.color.zCoord;
      double var22 = this.color.xCoord + var16 * var14;
      double var24 = this.color.yCoord + var18 * var14;
      double var26 = this.color.zCoord + var20 * var14;
      this.color = new Vec3(var22, var24, var26);
      return this.color;
   }
}
