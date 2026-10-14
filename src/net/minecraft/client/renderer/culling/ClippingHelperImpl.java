package net.minecraft.client.renderer.culling;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class ClippingHelperImpl extends ClippingHelper {
   private static ClippingHelperImpl instance = new ClippingHelperImpl();
   private FloatBuffer projectionMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
   private FloatBuffer modelviewMatrixBuffer = GLAllocation.createDirectFloatBuffer(16);
   private FloatBuffer field_78564_h = GLAllocation.createDirectFloatBuffer(16);

   public static ClippingHelper getInstance() {
      instance.init();
      return instance;
   }

   private void normalize(float[] floats) {
      float f = MathHelper.sqrt_float(floats[0] * floats[0] + floats[1] * floats[1] + floats[2] * floats[2]);
      floats[0] /= f;
      floats[1] /= f;
      floats[2] /= f;
      floats[3] /= f;
   }

   public void init() {
      ((Buffer)this.projectionMatrixBuffer).clear();
      ((Buffer)this.modelviewMatrixBuffer).clear();
      ((Buffer)this.field_78564_h).clear();
      GlStateManager.getFloat(2983, this.projectionMatrixBuffer);
      GlStateManager.getFloat(2982, this.modelviewMatrixBuffer);
      float[] floats = this.projectionMatrix;
      float[] floats2 = this.modelviewMatrix;
      ((Buffer)this.projectionMatrixBuffer).flip().limit(16);
      this.projectionMatrixBuffer.get(floats);
      ((Buffer)this.modelviewMatrixBuffer).flip().limit(16);
      this.modelviewMatrixBuffer.get(floats2);
      this.clippingMatrix[0] = floats2[0] * floats[0] + floats2[1] * floats[4] + floats2[2] * floats[8] + floats2[3] * floats[12];
      this.clippingMatrix[1] = floats2[0] * floats[1] + floats2[1] * floats[5] + floats2[2] * floats[9] + floats2[3] * floats[13];
      this.clippingMatrix[2] = floats2[0] * floats[2] + floats2[1] * floats[6] + floats2[2] * floats[10] + floats2[3] * floats[14];
      this.clippingMatrix[3] = floats2[0] * floats[3] + floats2[1] * floats[7] + floats2[2] * floats[11] + floats2[3] * floats[15];
      this.clippingMatrix[4] = floats2[4] * floats[0] + floats2[5] * floats[4] + floats2[6] * floats[8] + floats2[7] * floats[12];
      this.clippingMatrix[5] = floats2[4] * floats[1] + floats2[5] * floats[5] + floats2[6] * floats[9] + floats2[7] * floats[13];
      this.clippingMatrix[6] = floats2[4] * floats[2] + floats2[5] * floats[6] + floats2[6] * floats[10] + floats2[7] * floats[14];
      this.clippingMatrix[7] = floats2[4] * floats[3] + floats2[5] * floats[7] + floats2[6] * floats[11] + floats2[7] * floats[15];
      this.clippingMatrix[8] = floats2[8] * floats[0] + floats2[9] * floats[4] + floats2[10] * floats[8] + floats2[11] * floats[12];
      this.clippingMatrix[9] = floats2[8] * floats[1] + floats2[9] * floats[5] + floats2[10] * floats[9] + floats2[11] * floats[13];
      this.clippingMatrix[10] = floats2[8] * floats[2] + floats2[9] * floats[6] + floats2[10] * floats[10] + floats2[11] * floats[14];
      this.clippingMatrix[11] = floats2[8] * floats[3] + floats2[9] * floats[7] + floats2[10] * floats[11] + floats2[11] * floats[15];
      this.clippingMatrix[12] = floats2[12] * floats[0] + floats2[13] * floats[4] + floats2[14] * floats[8] + floats2[15] * floats[12];
      this.clippingMatrix[13] = floats2[12] * floats[1] + floats2[13] * floats[5] + floats2[14] * floats[9] + floats2[15] * floats[13];
      this.clippingMatrix[14] = floats2[12] * floats[2] + floats2[13] * floats[6] + floats2[14] * floats[10] + floats2[15] * floats[14];
      this.clippingMatrix[15] = floats2[12] * floats[3] + floats2[13] * floats[7] + floats2[14] * floats[11] + floats2[15] * floats[15];
      float[] floats3 = this.frustum[0];
      floats3[0] = this.clippingMatrix[3] - this.clippingMatrix[0];
      floats3[1] = this.clippingMatrix[7] - this.clippingMatrix[4];
      floats3[2] = this.clippingMatrix[11] - this.clippingMatrix[8];
      floats3[3] = this.clippingMatrix[15] - this.clippingMatrix[12];
      this.normalize(floats3);
      float[] floats4 = this.frustum[1];
      floats4[0] = this.clippingMatrix[3] + this.clippingMatrix[0];
      floats4[1] = this.clippingMatrix[7] + this.clippingMatrix[4];
      floats4[2] = this.clippingMatrix[11] + this.clippingMatrix[8];
      floats4[3] = this.clippingMatrix[15] + this.clippingMatrix[12];
      this.normalize(floats4);
      float[] floats5 = this.frustum[2];
      floats5[0] = this.clippingMatrix[3] + this.clippingMatrix[1];
      floats5[1] = this.clippingMatrix[7] + this.clippingMatrix[5];
      floats5[2] = this.clippingMatrix[11] + this.clippingMatrix[9];
      floats5[3] = this.clippingMatrix[15] + this.clippingMatrix[13];
      this.normalize(floats5);
      float[] floats6 = this.frustum[3];
      floats6[0] = this.clippingMatrix[3] - this.clippingMatrix[1];
      floats6[1] = this.clippingMatrix[7] - this.clippingMatrix[5];
      floats6[2] = this.clippingMatrix[11] - this.clippingMatrix[9];
      floats6[3] = this.clippingMatrix[15] - this.clippingMatrix[13];
      this.normalize(floats6);
      float[] floats7 = this.frustum[4];
      floats7[0] = this.clippingMatrix[3] - this.clippingMatrix[2];
      floats7[1] = this.clippingMatrix[7] - this.clippingMatrix[6];
      floats7[2] = this.clippingMatrix[11] - this.clippingMatrix[10];
      floats7[3] = this.clippingMatrix[15] - this.clippingMatrix[14];
      this.normalize(floats7);
      float[] floats8 = this.frustum[5];
      floats8[0] = this.clippingMatrix[3] + this.clippingMatrix[2];
      floats8[1] = this.clippingMatrix[7] + this.clippingMatrix[6];
      floats8[2] = this.clippingMatrix[11] + this.clippingMatrix[10];
      floats8[3] = this.clippingMatrix[15] + this.clippingMatrix[14];
      this.normalize(floats8);
   }
}
