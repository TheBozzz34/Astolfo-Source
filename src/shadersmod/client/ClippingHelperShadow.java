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

   @Override
   public boolean isBoxInFrustum(double d, double e, double d2, double d3, double d4, double d5) {
      for (int i = 0; i < this.shadowClipPlaneCount; i++) {
         float[] floats = this.shadowClipPlanes[i];
         if (this.dot4(floats, d, e, d2) <= 0.0
            && this.dot4(floats, d3, e, d2) <= 0.0
            && this.dot4(floats, d, d4, d2) <= 0.0
            && this.dot4(floats, d3, d4, d2) <= 0.0
            && this.dot4(floats, d, e, d5) <= 0.0
            && this.dot4(floats, d3, e, d5) <= 0.0
            && this.dot4(floats, d, d4, d5) <= 0.0
            && this.dot4(floats, d3, d4, d5) <= 0.0) {
            return false;
         }
      }

      return true;
   }

   private double dot4(float[] plane, double x, double y, double z) {
      return plane[0] * x + plane[1] * y + plane[2] * z + plane[3];
   }

   private double dot3(float[] vecA, float[] vecB) {
      return (double)vecA[0] * vecB[0] + (double)vecA[1] * vecB[1] + (double)vecA[2] * vecB[2];
   }

   public static ClippingHelper getInstance() {
      instance.init();
      return instance;
   }

   private void normalize3(float[] plane) {
      float f = MathHelper.sqrt_float(plane[0] * plane[0] + plane[1] * plane[1] + plane[2] * plane[2]);
      if (f == 0.0F) {
         f = 1.0F;
      }

      plane[0] /= f;
      plane[1] /= f;
      plane[2] /= f;
   }

   private void assignPlane(float[] plane, float a, float b, float c, float d) {
      float length = (float)Math.sqrt(a * a + b * b + c * c);
      plane[0] = a / length;
      plane[1] = b / length;
      plane[2] = c / length;
      plane[3] = d / length;
   }

   private void copyPlane(float[] dst, float[] src) {
      dst[0] = src[0];
      dst[1] = src[1];
      dst[2] = src[2];
      dst[3] = src[3];
   }

   private void cross3(float[] out, float[] a, float[] b) {
      out[0] = a[1] * b[2] - a[2] * b[1];
      out[1] = a[2] * b[0] - a[0] * b[2];
      out[2] = a[0] * b[1] - a[1] * b[0];
   }

   private float length(float x, float y, float z) {
      return (float)Math.sqrt(x * x + y * y + z * z);
   }

   private float distance(float x1, float y1, float z1, float x2, float y2, float z2) {
      return this.length(x1 - x2, y1 - y2, z1 - z2);
   }

   private void makeShadowPlane(float[] shadowPlane, float[] positivePlane, float[] negativePlane, float[] vecSun) {
      this.cross3(this.vecIntersection, positivePlane, negativePlane);
      this.cross3(shadowPlane, this.vecIntersection, vecSun);
      this.normalize3(shadowPlane);
      float f = (float)this.dot3(positivePlane, negativePlane);
      float g = (float)this.dot3(shadowPlane, negativePlane);
      float h = this.distance(shadowPlane[0], shadowPlane[1], shadowPlane[2], negativePlane[0] * g, negativePlane[1] * g, negativePlane[2] * g);
      float f2 = this.distance(
         positivePlane[0], positivePlane[1], positivePlane[2], negativePlane[0] * f, negativePlane[1] * f, negativePlane[2] * f
      );
      float f3 = h / f2;
      float f4 = (float)this.dot3(shadowPlane, positivePlane);
      float f5 = this.distance(shadowPlane[0], shadowPlane[1], shadowPlane[2], positivePlane[0] * f4, positivePlane[1] * f4, positivePlane[2] * f4);
      float f6 = this.distance(
         negativePlane[0], negativePlane[1], negativePlane[2], positivePlane[0] * f, positivePlane[1] * f, positivePlane[2] * f
      );
      float f7 = f5 / f6;
      shadowPlane[3] = positivePlane[3] * f3 + negativePlane[3] * f7;
   }

   public void init() {
      float[] floats = this.projectionMatrix;
      float[] floats2 = this.modelviewMatrix;
      float[] floats3 = this.clippingMatrix;
      System.arraycopy(Shaders.faProjection, 0, floats, 0, 16);
      System.arraycopy(Shaders.faModelView, 0, floats2, 0, 16);
      SMath.multiplyMat4xMat4(floats3, floats2, floats);
      this.assignPlane(this.frustum[0], floats3[3] - floats3[0], floats3[7] - floats3[4], floats3[11] - floats3[8], floats3[15] - floats3[12]);
      this.assignPlane(this.frustum[1], floats3[3] + floats3[0], floats3[7] + floats3[4], floats3[11] + floats3[8], floats3[15] + floats3[12]);
      this.assignPlane(this.frustum[2], floats3[3] + floats3[1], floats3[7] + floats3[5], floats3[11] + floats3[9], floats3[15] + floats3[13]);
      this.assignPlane(this.frustum[3], floats3[3] - floats3[1], floats3[7] - floats3[5], floats3[11] - floats3[9], floats3[15] - floats3[13]);
      this.assignPlane(this.frustum[4], floats3[3] - floats3[2], floats3[7] - floats3[6], floats3[11] - floats3[10], floats3[15] - floats3[14]);
      this.assignPlane(this.frustum[5], floats3[3] + floats3[2], floats3[7] + floats3[6], floats3[11] + floats3[10], floats3[15] + floats3[14]);
      float[] floats4 = Shaders.shadowLightPositionVector;
      float f = (float)this.dot3(this.frustum[0], floats4);
      float g = (float)this.dot3(this.frustum[1], floats4);
      float h = (float)this.dot3(this.frustum[2], floats4);
      float f2 = (float)this.dot3(this.frustum[3], floats4);
      float f3 = (float)this.dot3(this.frustum[4], floats4);
      float f4 = (float)this.dot3(this.frustum[5], floats4);
      this.shadowClipPlaneCount = 0;
      if (f >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0]);
         if (f > 0.0F) {
            if (h < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[2], floats4);
            }

            if (f2 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[3], floats4);
            }

            if (f3 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[4], floats4);
            }

            if (f4 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[0], this.frustum[5], floats4);
            }
         }
      }

      if (g >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1]);
         if (g > 0.0F) {
            if (h < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[2], floats4);
            }

            if (f2 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[3], floats4);
            }

            if (f3 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[4], floats4);
            }

            if (f4 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[1], this.frustum[5], floats4);
            }
         }
      }

      if (h >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2]);
         if (h > 0.0F) {
            if (f < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[0], floats4);
            }

            if (g < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[1], floats4);
            }

            if (f3 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[4], floats4);
            }

            if (f4 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[2], this.frustum[5], floats4);
            }
         }
      }

      if (f2 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3]);
         if (f2 > 0.0F) {
            if (f < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[0], floats4);
            }

            if (g < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[1], floats4);
            }

            if (f3 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[4], floats4);
            }

            if (f4 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[3], this.frustum[5], floats4);
            }
         }
      }

      if (f3 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4]);
         if (f3 > 0.0F) {
            if (f < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[0], floats4);
            }

            if (g < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[1], floats4);
            }

            if (h < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[2], floats4);
            }

            if (f2 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[4], this.frustum[3], floats4);
            }
         }
      }

      if (f4 >= 0.0F) {
         this.copyPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5]);
         if (f4 > 0.0F) {
            if (f < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[0], floats4);
            }

            if (g < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[1], floats4);
            }

            if (h < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[2], floats4);
            }

            if (f2 < 0.0F) {
               this.makeShadowPlane(this.shadowClipPlanes[this.shadowClipPlaneCount++], this.frustum[5], this.frustum[3], floats4);
            }
         }
      }
   }
}
