package net.minecraft.util;

public class Vec3 {
   public final double xCoord;
   public final double yCoord;
   public final double zCoord;
   private static final long a = 5838913629429L;

   public Vec3(double var1, double var3, double var5) {
      if (var1 == -0.0) {
         var1 = 0.0;
      }

      if (var3 == -0.0) {
         var3 = 0.0;
      }

      if (var5 == -0.0) {
         var5 = 0.0;
      }

      this.xCoord = var1;
      this.yCoord = var3;
      this.zCoord = var5;
   }

   public Vec3(Vec3i var1) {
      this(var1.getX(), var1.getY(), var1.getZ());
   }

   public Vec3 e(double var1) {
      return new Vec3(this.xCoord * var1, this.yCoord * var1, this.zCoord * var1);
   }

   public Vec3 subtractReverse(Vec3 var1) {
      return new Vec3(var1.xCoord - this.xCoord, var1.yCoord - this.yCoord, var1.zCoord - this.zCoord);
   }

   public Vec3 normalize() {
      double var3 = MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
      return var3 < 1.0E-4 ? new Vec3(0.0, 0.0, 0.0) : new Vec3(this.xCoord / var3, this.yCoord / var3, this.zCoord / var3);
   }

   public double dotProduct(Vec3 var1) {
      return this.xCoord * var1.xCoord + this.yCoord * var1.yCoord + this.zCoord * var1.zCoord;
   }

   public Vec3 crossProduct(Vec3 var1) {
      return new Vec3(
         this.yCoord * var1.zCoord - this.zCoord * var1.yCoord,
         this.zCoord * var1.xCoord - this.xCoord * var1.zCoord,
         this.xCoord * var1.yCoord - this.yCoord * var1.xCoord
      );
   }

   public Vec3 subtract(Vec3 var1) {
      return this.subtract(var1.xCoord, var1.yCoord, var1.zCoord);
   }

   public Vec3 subtract(double var1, double var3, double var5) {
      return this.addVector(-var1, -var3, -var5);
   }

   public Vec3 l(Vec3 var1) {
      return this.addVector(var1.xCoord, var1.yCoord, var1.zCoord);
   }

   public Vec3 addVector(double var1, double var3, double var5) {
      return new Vec3(this.xCoord + var1, this.yCoord + var3, this.zCoord + var5);
   }

   public double distanceTo(Vec3 var1) {
      double var2 = var1.xCoord - this.xCoord;
      double var4 = var1.yCoord - this.yCoord;
      double var6 = var1.zCoord - this.zCoord;
      return MathHelper.sqrt_double(var2 * var2 + var4 * var4 + var6 * var6);
   }

   public double squareDistanceTo(Vec3 var1) {
      double var2 = var1.xCoord - this.xCoord;
      double var4 = var1.yCoord - this.yCoord;
      double var6 = var1.zCoord - this.zCoord;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public double lengthVector() {
      return MathHelper.sqrt_double(this.xCoord * this.xCoord + this.yCoord * this.yCoord + this.zCoord * this.zCoord);
   }

   public Vec3 getIntermediateWithXValue(Vec3 var1, double var2) {
      double var7 = var1.xCoord - this.xCoord;
      double var9 = var1.yCoord - this.yCoord;
      double var11 = var1.zCoord - this.zCoord;
      if (var7 * var7 < 1.0E-7F) {
         return null;
      }

      double var13 = (var2 - this.xCoord) / var7;
      return var13 >= 0.0 && var13 <= 1.0 ? new Vec3(this.xCoord + var7 * var13, this.yCoord + var9 * var13, this.zCoord + var11 * var13) : null;
   }

   public Vec3 getIntermediateWithYValue(Vec3 var1, double var2) {
      double var7 = var1.xCoord - this.xCoord;
      double var9 = var1.yCoord - this.yCoord;
      double var11 = var1.zCoord - this.zCoord;
      if (var9 * var9 < 1.0E-7F) {
         return null;
      }

      double var13 = (var2 - this.yCoord) / var9;
      return var13 >= 0.0 && var13 <= 1.0 ? new Vec3(this.xCoord + var7 * var13, this.yCoord + var9 * var13, this.zCoord + var11 * var13) : null;
   }

   public Vec3 getIntermediateWithZValue(Vec3 var1, double var2) {
      double var7 = var1.xCoord - this.xCoord;
      double var9 = var1.yCoord - this.yCoord;
      double var11 = var1.zCoord - this.zCoord;
      if (var11 * var11 < 1.0E-7F) {
         return null;
      }

      double var13 = (var2 - this.zCoord) / var11;
      return var13 >= 0.0 && var13 <= 1.0 ? new Vec3(this.xCoord + var7 * var13, this.yCoord + var9 * var13, this.zCoord + var11 * var13) : null;
   }

   @Override
   public String toString() {
      return "(" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord + ")";
   }

   public Vec3 rotatePitch(float var1) {
      float var2 = MathHelper.cos(var1);
      float var3 = MathHelper.sin(var1);
      double var4 = this.xCoord;
      double var6 = this.yCoord * var2 + this.zCoord * var3;
      double var8 = this.zCoord * var2 - this.yCoord * var3;
      return new Vec3(var4, var6, var8);
   }

   public Vec3 rotateYaw(float var1) {
      float var2 = MathHelper.cos(var1);
      float var3 = MathHelper.sin(var1);
      double var4 = this.xCoord * var2 + this.zCoord * var3;
      double var6 = this.yCoord;
      double var8 = this.zCoord * var2 - this.xCoord * var3;
      return new Vec3(var4, var6, var8);
   }
}
