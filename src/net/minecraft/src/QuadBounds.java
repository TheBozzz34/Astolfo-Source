package net.minecraft.src;

import net.minecraft.util.EnumFacing;

public class QuadBounds {
   private float j = Float.MAX_VALUE;
   private float z = Float.MAX_VALUE;
   private float E = Float.MAX_VALUE;
   private float k = -Float.MAX_VALUE;
   private float x = -Float.MAX_VALUE;
   private float A = -Float.MAX_VALUE;
   private static final long a = 103981064499023L;

   public QuadBounds(int[] var1) {
      int var5 = var1.length / 4;

      for (int var6 = 0; var6 < 4; var6++) {
         int var7 = var6 * var5;
         float var8 = Float.intBitsToFloat(var1[var7 + 0]);
         float var9 = Float.intBitsToFloat(var1[var7 + 1]);
         float var10 = Float.intBitsToFloat(var1[var7 + 2]);
         if (this.j > var8) {
            this.j = var8;
         }

         if (this.z > var9) {
            this.z = var9;
         }

         if (this.E > var10) {
            this.E = var10;
         }

         if (this.k < var8) {
            this.k = var8;
         }

         if (this.x < var9) {
            this.x = var9;
         }

         if (this.A < var10) {
            this.A = var10;
         }
      }
   }

   public float x() {
      return this.j;
   }

   public float T() {
      return this.z;
   }

   public float J() {
      return this.E;
   }

   public float G() {
      return this.k;
   }

   public float R() {
      return this.x;
   }

   public float s() {
      return this.A;
   }

   public boolean j(EnumFacing var1) {
      float var5;
      float var6;
      float var7;
      switch (var1) {
         case DOWN:
            var5 = this.T();
            var6 = this.R();
            var7 = 0.0F;
            break;
         case UP:
            var5 = this.T();
            var6 = this.R();
            var7 = 1.0F;
            break;
         case NORTH:
            var5 = this.J();
            var6 = this.s();
            var7 = 0.0F;
            break;
         case SOUTH:
            var5 = this.J();
            var6 = this.s();
            var7 = 1.0F;
            break;
         case WEST:
            var5 = this.x();
            var6 = this.G();
            var7 = 0.0F;
            break;
         case EAST:
            var5 = this.x();
            var6 = this.G();
            var7 = 1.0F;
            break;
         default:
            return false;
      }

      return var5 == var7 && var6 == var7;
   }

   public boolean K(EnumFacing var1) {
      float var5;
      float var6;
      float var7;
      float var8;
      switch (var1) {
         case DOWN:
         case UP:
            var5 = this.x();
            var6 = this.G();
            var7 = this.J();
            var8 = this.s();
            break;
         case NORTH:
         case SOUTH:
            var5 = this.x();
            var6 = this.G();
            var7 = this.T();
            var8 = this.R();
            break;
         case WEST:
         case EAST:
            var5 = this.T();
            var6 = this.R();
            var7 = this.J();
            var8 = this.s();
            break;
         default:
            return false;
      }

      return var5 == 0.0F && var6 == 1.0F && var7 == 0.0F && var8 == 1.0F;
   }
}
