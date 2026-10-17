package net.minecraft.src;

import net.minecraft.util.EnumFacing;

public class QuadBounds {
   private float minX = Float.MAX_VALUE;
   private float z = Float.MAX_VALUE;
   private float minZ = Float.MAX_VALUE;
   private float maxX = -Float.MAX_VALUE;
   private float x = -Float.MAX_VALUE;
   private float maxZ = -Float.MAX_VALUE;

   public QuadBounds(int[] vertexData) {
      int i = vertexData.length / 4;

      for (int j = 0; j < 4; j++) {
         int k = j * i;
         float f = Float.intBitsToFloat(vertexData[k + 0]);
         float g = Float.intBitsToFloat(vertexData[k + 1]);
         float h = Float.intBitsToFloat(vertexData[k + 2]);
         if (this.minX > f) {
            this.minX = f;
         }

         if (this.z > g) {
            this.z = g;
         }

         if (this.minZ > h) {
            this.minZ = h;
         }

         if (this.maxX < f) {
            this.maxX = f;
         }

         if (this.x < g) {
            this.x = g;
         }

         if (this.maxZ < h) {
            this.maxZ = h;
         }
      }
   }

   public float getMinX() {
      return this.minX;
   }

   public float getMinY() {
      return this.z;
   }

   public float getMinZ() {
      return this.minZ;
   }

   public float getMaxX() {
      return this.maxX;
   }

   public float getMaxY() {
      return this.x;
   }

   public float getMaxZ() {
      return this.maxZ;
   }

   public boolean isFaceQuad(EnumFacing face) {
      float f;
      float g;
      float h;
      switch (face) {
         case DOWN:
            f = this.getMinY();
            g = this.getMaxY();
            h = 0.0F;
            break;
         case UP:
            f = this.getMinY();
            g = this.getMaxY();
            h = 1.0F;
            break;
         case NORTH:
            f = this.getMinZ();
            g = this.getMaxZ();
            h = 0.0F;
            break;
         case SOUTH:
            f = this.getMinZ();
            g = this.getMaxZ();
            h = 1.0F;
            break;
         case WEST:
            f = this.getMinX();
            g = this.getMaxX();
            h = 0.0F;
            break;
         case EAST:
            f = this.getMinX();
            g = this.getMaxX();
            h = 1.0F;
            break;
         default:
            return false;
      }

      return f == h && g == h;
   }

   public boolean isFullQuad(EnumFacing face) {
      float f;
      float g;
      float h;
      float f2;
      switch (face) {
         case DOWN:
         case UP:
            f = this.getMinX();
            g = this.getMaxX();
            h = this.getMinZ();
            f2 = this.getMaxZ();
            break;
         case NORTH:
         case SOUTH:
            f = this.getMinX();
            g = this.getMaxX();
            h = this.getMinY();
            f2 = this.getMaxY();
            break;
         case WEST:
         case EAST:
            f = this.getMinY();
            g = this.getMaxY();
            h = this.getMinZ();
            f2 = this.getMaxZ();
            break;
         default:
            return false;
      }

      return f == 0.0F && g == 1.0F && h == 0.0F && f2 == 1.0F;
   }
}
