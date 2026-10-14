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

   public QuadBounds(int[] vertexData) {
      int i = vertexData.length / 4;

      for (int l = 0; l < 4; l++) {
         int m = l * i;
         float f = Float.intBitsToFloat(vertexData[m + 0]);
         float g = Float.intBitsToFloat(vertexData[m + 1]);
         float h = Float.intBitsToFloat(vertexData[m + 2]);
         if (this.j > f) {
            this.j = f;
         }

         if (this.z > g) {
            this.z = g;
         }

         if (this.E > h) {
            this.E = h;
         }

         if (this.k < f) {
            this.k = f;
         }

         if (this.x < g) {
            this.x = g;
         }

         if (this.A < h) {
            this.A = h;
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

   public boolean j(EnumFacing enumFacing) {
      float f;
      float g;
      float h;
      switch (enumFacing) {
         case DOWN:
            f = this.T();
            g = this.R();
            h = 0.0F;
            break;
         case UP:
            f = this.T();
            g = this.R();
            h = 1.0F;
            break;
         case NORTH:
            f = this.J();
            g = this.s();
            h = 0.0F;
            break;
         case SOUTH:
            f = this.J();
            g = this.s();
            h = 1.0F;
            break;
         case WEST:
            f = this.x();
            g = this.G();
            h = 0.0F;
            break;
         case EAST:
            f = this.x();
            g = this.G();
            h = 1.0F;
            break;
         default:
            return false;
      }

      return f == h && g == h;
   }

   public boolean K(EnumFacing enumFacing) {
      float f;
      float g;
      float h;
      float f2;
      switch (enumFacing) {
         case DOWN:
         case UP:
            f = this.x();
            g = this.G();
            h = this.J();
            f2 = this.s();
            break;
         case NORTH:
         case SOUTH:
            f = this.x();
            g = this.G();
            h = this.T();
            f2 = this.R();
            break;
         case WEST:
         case EAST:
            f = this.T();
            g = this.R();
            h = this.J();
            f2 = this.s();
            break;
         default:
            return false;
      }

      return f == 0.0F && g == 1.0F && h == 0.0F && f2 == 1.0F;
   }
}
