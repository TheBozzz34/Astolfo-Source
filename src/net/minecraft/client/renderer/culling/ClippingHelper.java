package net.minecraft.client.renderer.culling;

public class ClippingHelper {
   public float[][] frustum = new float[6][4];
   public float[] projectionMatrix = new float[16];
   public float[] modelviewMatrix = new float[16];
   public float[] clippingMatrix = new float[16];

   private float dot(float[] floats, float f, float g, float h) {
      return floats[0] * f + floats[1] * g + floats[2] * h + floats[3];
   }

   public boolean isBoxInFrustum(double d, double d2, double d3, double d4, double d5, double d6) {
      float f = (float)d;
      float g = (float)d2;
      float h = (float)d3;
      float f2 = (float)d4;
      float f3 = (float)d5;
      float f4 = (float)d6;

      for (int i = 0; i < 6; i++) {
         float[] floats = this.frustum[i];
         if (this.dot(floats, f, g, h) <= 0.0F
            && this.dot(floats, f2, g, h) <= 0.0F
            && this.dot(floats, f, f3, h) <= 0.0F
            && this.dot(floats, f2, f3, h) <= 0.0F
            && this.dot(floats, f, g, f4) <= 0.0F
            && this.dot(floats, f2, g, f4) <= 0.0F
            && this.dot(floats, f, f3, f4) <= 0.0F
            && this.dot(floats, f2, f3, f4) <= 0.0F) {
            return false;
         }
      }

      return true;
   }
}
