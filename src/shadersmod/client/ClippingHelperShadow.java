package shadersmod.client;

import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.util.MathHelper;

public class ClippingHelperShadow extends ClippingHelper {
   private static ClippingHelperShadow instance = new ClippingHelperShadow();
   public float[] frustumTest = new float[6];
   public float[][] shadowClipPlanes = new float[10][4];
   public int shadowClipPlaneCount;
   public float[] matInvMP = new float[16];
   public float[] vecIntersection = new float[4];
   private static final long b = 49460763530509L;

   @Override
   public boolean isBoxInFrustum(double var1, double var3, double var5, double var7, double var9, double var11) {
      for (int var16 = 0; var16 < this.shadowClipPlaneCount; var16++) {
         float[] var17 = this.shadowClipPlanes[var16];
         if (this.dot4(var17, var1, var3, var5) <= 0.0
            && this.dot4(var17, var7, var3, var5) <= 0.0
            && this.dot4(var17, var1, var9, var5) <= 0.0
            && this.dot4(var17, var7, var9, var5) <= 0.0
            && this.dot4(var17, var1, var3, var11) <= 0.0
            && this.dot4(var17, var7, var3, var11) <= 0.0
            && this.dot4(var17, var1, var9, var11) <= 0.0
            && this.dot4(var17, var7, var9, var11) <= 0.0) {
            return false;
         }
      }

      return true;
   }

   private double dot4(float[] var1, double var2, double var4, double var6) {
      return var1[0] * var2 + var1[1] * var4 + var1[2] * var6 + var1[3];
   }

   private double dot3(float[] var1, float[] var2) {
      return (double)var1[0] * var2[0] + (double)var1[1] * var2[1] + (double)var1[2] * var2[2];
   }

   public static ClippingHelper getInstance() {
      instance.init();
      return instance;
   }

   private void normalize3(float[] var1) {
      float var5 = MathHelper.sqrt_float(var1[0] * var1[0] + var1[1] * var1[1] + var1[2] * var1[2]);
      if (var5 == 0.0F) {
         var5 = 1.0F;
      }

      var1[0] /= var5;
      var1[1] /= var5;
      var1[2] /= var5;
   }

   private void assignPlane(float[] var1, float var2, float var3, float var4, float var5) {
      float var6 = (float)Math.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
      var1[0] = var2 / var6;
      var1[1] = var3 / var6;
      var1[2] = var4 / var6;
      var1[3] = var5 / var6;
   }

   private void copyPlane(float[] var1, float[] var2) {
      var1[0] = var2[0];
      var1[1] = var2[1];
      var1[2] = var2[2];
      var1[3] = var2[3];
   }

   private void cross3(float[] var1, float[] var2, float[] var3) {
      var1[0] = var2[1] * var3[2] - var2[2] * var3[1];
      var1[1] = var2[2] * var3[0] - var2[0] * var3[2];
      var1[2] = var2[0] * var3[1] - var2[1] * var3[0];
   }

   private float length(float var1, float var2, float var3) {
      return (float)Math.sqrt(var1 * var1 + var2 * var2 + var3 * var3);
   }

   private float distance(float var1, float var2, float var3, float var4, float var5, float var6) {
      return this.length(var1 - var4, var2 - var5, var3 - var6);
   }

   private void makeShadowPlane(float[] var1, float[] var2, float[] var3, float[] var4) {
      this.cross3(this.vecIntersection, var2, var3);
      this.cross3(var1, this.vecIntersection, var4);
      this.normalize3(var1);
      float var5 = (float)this.dot3(var2, var3);
      float var6 = (float)this.dot3(var1, var3);
      float var7 = this.distance(var1[0], var1[1], var1[2], var3[0] * var6, var3[1] * var6, var3[2] * var6);
      float var8 = this.distance(var2[0], var2[1], var2[2], var3[0] * var5, var3[1] * var5, var3[2] * var5);
      float var9 = var7 / var8;
      float var10 = (float)this.dot3(var1, var2);
      float var11 = this.distance(var1[0], var1[1], var1[2], var2[0] * var10, var2[1] * var10, var2[2] * var10);
      float var12 = this.distance(var3[0], var3[1], var3[2], var2[0] * var5, var2[1] * var5, var2[2] * var5);
      float var13 = var11 / var12;
      var1[3] = var2[3] * var9 + var3[3] * var13;
   }

   public void init() {
      float[] var4 = this.projectionMatrix;
      float[] var5 = this.modelviewMatrix;
      float[] var6 = this.clippingMatrix;
      System.arraycopy(Shaders.faProjection, 0, var4, 0, 16);
      System.arraycopy(Shaders.faModelView, 0, var5, 0, 16);
      SMath.multiplyMat4xMat4(var6, var5, var4);
      this.assignPlane(this.frustum[0], var6[3] - var6[0], var6[7] - var6[4], var6[11] - var6[8], var6[15] - var6[12]);
      this.assignPlane(this.frustum[1], var6[3] + var6[0], var6[7] + var6[4], var6[11] + var6[8], var6[15] + var6[12]);
      this.assignPlane(this.frustum[2], var6[3] + var6[1], var6[7] + var6[5], var6[11] + var6[9], var6[15] + var6[13]);
      this.assignPlane(this.frustum[3], var6[3] - var6[1], var6[7] - var6[5], var6[11] - var6[9], var6[15] - var6[13]);
      this.assignPlane(this.frustum[4], var6[3] - var6[2], var6[7] - var6[6], var6[11] - var6[10], var6[15] - var6[14]);
      this.assignPlane(this.frustum[5], var6[3] + var6[2], var6[7] + var6[6], var6[11] + var6[10], var6[15] + var6[14]);
      float[] var7 = Shaders.shadowLightPositionVector;
      float var8 = (float)this.dot3(this.frustum[0], var7);
      float var9 = (float)this.dot3(this.frustum[1], var7);
      float var10 = (float)this.dot3(this.frustum[2], var7);
      float var11 = (float)this.dot3(this.frustum[3], var7);
      float var12 = (float)this.dot3(this.frustum[4], var7);
      float var13 = (float)this.dot3(this.frustum[5], var7);
      this.shadowClipPlaneCount = 0;
      if (var8 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0]);
         if (var8 > 0.0F) {
            if (var10 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[2], var7);
            }

            if (var11 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[3], var7);
            }

            if (var12 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[4], var7);
            }

            if (var13 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[5], var7);
            }
         }
      }

      if (var9 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1]);
         if (var9 > 0.0F) {
            if (var10 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[2], var7);
            }

            if (var11 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[3], var7);
            }

            if (var12 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[4], var7);
            }

            if (var13 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[5], var7);
            }
         }
      }

      if (var10 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2]);
         if (var10 > 0.0F) {
            if (var8 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[0], var7);
            }

            if (var9 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[1], var7);
            }

            if (var12 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[4], var7);
            }

            if (var13 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[5], var7);
            }
         }
      }

      if (var11 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3]);
         if (var11 > 0.0F) {
            if (var8 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[0], var7);
            }

            if (var9 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[1], var7);
            }

            if (var12 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[4], var7);
            }

            if (var13 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[5], var7);
            }
         }
      }

      if (var12 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4]);
         if (var12 > 0.0F) {
            if (var8 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[0], var7);
            }

            if (var9 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[1], var7);
            }

            if (var10 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[2], var7);
            }

            if (var11 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[3], var7);
            }
         }
      }

      if (var13 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5]);
         if (var13 > 0.0F) {
            if (var8 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[0], var7);
            }

            if (var9 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[1], var7);
            }

            if (var10 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[2], var7);
            }

            if (var11 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[3], var7);
            }
         }
      }
   }
}
