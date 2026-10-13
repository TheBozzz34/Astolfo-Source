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
   private static final long b = 13205404394962L;

   public static ClippingHelper getInstance() {
      instance.init();
      return instance;
   }

   private void normalize(float[] var1) {
      float var2 = MathHelper.sqrt_float(var1[0] * var1[0] + var1[1] * var1[1] + var1[2] * var1[2]);
      var1[0] /= var2;
      var1[1] /= var2;
      var1[2] /= var2;
      var1[3] /= var2;
   }

   public void init() {
      ((Buffer)this.projectionMatrixBuffer).clear();
      ((Buffer)this.modelviewMatrixBuffer).clear();
      ((Buffer)this.field_78564_h).clear();
      GlStateManager.getFloat(2983, this.projectionMatrixBuffer);
      GlStateManager.getFloat(2982, this.modelviewMatrixBuffer);
      float[] var5 = this.projectionMatrix;
      float[] var6 = this.modelviewMatrix;
      ((Buffer)this.projectionMatrixBuffer).flip().limit(16);
      this.projectionMatrixBuffer.get(var5);
      ((Buffer)this.modelviewMatrixBuffer).flip().limit(16);
      this.modelviewMatrixBuffer.get(var6);
      this.clippingMatrix[0] = var6[0] * var5[0] + var6[1] * var5[4] + var6[2] * var5[8] + var6[3] * var5[12];
      this.clippingMatrix[1] = var6[0] * var5[1] + var6[1] * var5[5] + var6[2] * var5[9] + var6[3] * var5[13];
      this.clippingMatrix[2] = var6[0] * var5[2] + var6[1] * var5[6] + var6[2] * var5[10] + var6[3] * var5[14];
      this.clippingMatrix[3] = var6[0] * var5[3] + var6[1] * var5[7] + var6[2] * var5[11] + var6[3] * var5[15];
      this.clippingMatrix[4] = var6[4] * var5[0] + var6[5] * var5[4] + var6[6] * var5[8] + var6[7] * var5[12];
      this.clippingMatrix[5] = var6[4] * var5[1] + var6[5] * var5[5] + var6[6] * var5[9] + var6[7] * var5[13];
      this.clippingMatrix[6] = var6[4] * var5[2] + var6[5] * var5[6] + var6[6] * var5[10] + var6[7] * var5[14];
      this.clippingMatrix[7] = var6[4] * var5[3] + var6[5] * var5[7] + var6[6] * var5[11] + var6[7] * var5[15];
      this.clippingMatrix[8] = var6[8] * var5[0] + var6[9] * var5[4] + var6[10] * var5[8] + var6[11] * var5[12];
      this.clippingMatrix[9] = var6[8] * var5[1] + var6[9] * var5[5] + var6[10] * var5[9] + var6[11] * var5[13];
      this.clippingMatrix[10] = var6[8] * var5[2] + var6[9] * var5[6] + var6[10] * var5[10] + var6[11] * var5[14];
      this.clippingMatrix[11] = var6[8] * var5[3] + var6[9] * var5[7] + var6[10] * var5[11] + var6[11] * var5[15];
      this.clippingMatrix[12] = var6[12] * var5[0] + var6[13] * var5[4] + var6[14] * var5[8] + var6[15] * var5[12];
      this.clippingMatrix[13] = var6[12] * var5[1] + var6[13] * var5[5] + var6[14] * var5[9] + var6[15] * var5[13];
      this.clippingMatrix[14] = var6[12] * var5[2] + var6[13] * var5[6] + var6[14] * var5[10] + var6[15] * var5[14];
      this.clippingMatrix[15] = var6[12] * var5[3] + var6[13] * var5[7] + var6[14] * var5[11] + var6[15] * var5[15];
      float[] var7 = this.frustum[0];
      var7[0] = this.clippingMatrix[3] - this.clippingMatrix[0];
      var7[1] = this.clippingMatrix[7] - this.clippingMatrix[4];
      var7[2] = this.clippingMatrix[11] - this.clippingMatrix[8];
      var7[3] = this.clippingMatrix[15] - this.clippingMatrix[12];
      this.normalize(var7);
      float[] var8 = this.frustum[1];
      var8[0] = this.clippingMatrix[3] + this.clippingMatrix[0];
      var8[1] = this.clippingMatrix[7] + this.clippingMatrix[4];
      var8[2] = this.clippingMatrix[11] + this.clippingMatrix[8];
      var8[3] = this.clippingMatrix[15] + this.clippingMatrix[12];
      this.normalize(var8);
      float[] var9 = this.frustum[2];
      var9[0] = this.clippingMatrix[3] + this.clippingMatrix[1];
      var9[1] = this.clippingMatrix[7] + this.clippingMatrix[5];
      var9[2] = this.clippingMatrix[11] + this.clippingMatrix[9];
      var9[3] = this.clippingMatrix[15] + this.clippingMatrix[13];
      this.normalize(var9);
      float[] var10 = this.frustum[3];
      var10[0] = this.clippingMatrix[3] - this.clippingMatrix[1];
      var10[1] = this.clippingMatrix[7] - this.clippingMatrix[5];
      var10[2] = this.clippingMatrix[11] - this.clippingMatrix[9];
      var10[3] = this.clippingMatrix[15] - this.clippingMatrix[13];
      this.normalize(var10);
      float[] var11 = this.frustum[4];
      var11[0] = this.clippingMatrix[3] - this.clippingMatrix[2];
      var11[1] = this.clippingMatrix[7] - this.clippingMatrix[6];
      var11[2] = this.clippingMatrix[11] - this.clippingMatrix[10];
      var11[3] = this.clippingMatrix[15] - this.clippingMatrix[14];
      this.normalize(var11);
      float[] var12 = this.frustum[5];
      var12[0] = this.clippingMatrix[3] + this.clippingMatrix[2];
      var12[1] = this.clippingMatrix[7] + this.clippingMatrix[6];
      var12[2] = this.clippingMatrix[11] + this.clippingMatrix[10];
      var12[3] = this.clippingMatrix[15] + this.clippingMatrix[14];
      this.normalize(var12);
   }
}
