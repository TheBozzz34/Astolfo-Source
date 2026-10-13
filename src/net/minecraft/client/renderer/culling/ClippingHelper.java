package net.minecraft.client.renderer.culling;

public class ClippingHelper {
   public float[][] frustum = new float[6][4];
   public float[] projectionMatrix = new float[16];
   public float[] modelviewMatrix = new float[16];
   public float[] clippingMatrix = new float[16];
   private static final long a = 118081372493390L;

   private float dot(float[] var1, float var2, float var3, float var4) {
      return var1[0] * var2 + var1[1] * var3 + var1[2] * var4 + var1[3];
   }

   public boolean isBoxInFrustum(double var1, double var3, double var5, double var7, double var9, double var11) {
      float var16 = (float)var1;
      float var17 = (float)var3;
      float var18 = (float)var5;
      float var19 = (float)var7;
      float var20 = (float)var9;
      float var21 = (float)var11;

      for (int var22 = 0; var22 < 6; var22++) {
         float[] var23 = this.frustum[var22];
         if (this.dot(var23, var16, var17, var18) <= 0.0F
            && this.dot(var23, var19, var17, var18) <= 0.0F
            && this.dot(var23, var16, var20, var18) <= 0.0F
            && this.dot(var23, var19, var20, var18) <= 0.0F
            && this.dot(var23, var16, var17, var21) <= 0.0F
            && this.dot(var23, var19, var17, var21) <= 0.0F
            && this.dot(var23, var16, var20, var21) <= 0.0F
            && this.dot(var23, var19, var20, var21) <= 0.0F) {
            return false;
         }
      }

      return true;
   }
}
