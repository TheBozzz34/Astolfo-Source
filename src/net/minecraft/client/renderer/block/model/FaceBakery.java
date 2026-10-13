package net.minecraft.client.renderer.block.model;

import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;
import net.minecraftforge.client.model.ITransformation;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import shadersmod.client.Shaders;

public class FaceBakery {
   private static final float SCALE_ROTATION_22_5 = 1.0F / (float)Math.cos((float) (Math.PI / 8)) - 1.0F;
   private static final float SCALE_ROTATION_GENERAL = 1.0F / (float)Math.cos(Math.PI / 4) - 1.0F;
   private static final long a = 84453518255931L;

   public BakedQuad makeBakedQuad(
      Vector3f var1,
      Vector3f var2,
      BlockPartFace var3,
      TextureAtlasSprite var4,
      EnumFacing var5,
      ModelRotation var6,
      BlockPartRotation var7,
      boolean var8,
      boolean var9
   ) {
      int[] var12 = this.makeQuadVertexData(var3, var4, var5, this.getPositionsDiv16(var1, var2), var6, var7, var8, var9);
      EnumFacing var13 = getFacingFromVertexData(var12);
      if (var8) {
         this.lockUv(var12, var13, var3.blockFaceUV, var4);
      }

      if (var7 == null) {
         this.applyFacing(var12, var13);
      }

      return new BakedQuad(var12, var3.tintIndex, var13, var4);
   }

   public BakedQuad makeBakedQuad(
      Vector3f var1,
      Vector3f var2,
      BlockPartFace var3,
      TextureAtlasSprite var4,
      EnumFacing var5,
      ITransformation var6,
      BlockPartRotation var7,
      boolean var8,
      boolean var9
   ) {
      int[] var13 = this.makeQuadVertexData(var3, var4, var5, this.getPositionsDiv16(var1, var2), var6, var7, var8, var9);
      EnumFacing var14 = getFacingFromVertexData(var13);
      if (var8) {
         this.lockUv(var13, var14, var3.blockFaceUV, var4);
      }

      if (var7 == null) {
         this.applyFacing(var13, var14);
      }

      if (Reflector.ForgeHooksClient_fillNormal.exists()) {
         Reflector.callVoid(Reflector.ForgeHooksClient_fillNormal, var13, var14);
      }

      return new BakedQuad(var13, var3.tintIndex, var14, var4);
   }

   private int[] makeQuadVertexData(
      BlockPartFace var1, TextureAtlasSprite var2, EnumFacing var3, float[] var4, ITransformation var5, BlockPartRotation var6, boolean var7, boolean var8
   ) {
      byte var12 = 28;
      if (Config.isShaders()) {
         var12 = 56;
      }

      int[] var13 = new int[var12];

      for (int var14 = 0; var14 < 4; var14++) {
         this.fillVertexData(var13, var14, var3, var1, var4, var2, var5, var6, var7, var8);
      }

      return var13;
   }

   private int getFaceShadeColor(EnumFacing var1) {
      float var4 = this.getFaceBrightness(var1);
      int var5 = MathHelper.clamp_int((int)(var4 * 255.0F), 0, 255);
      return 0xFF000000 | var5 << 16 | var5 << 8 | var5;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private float getFaceBrightness(EnumFacing var1) {
      switch (var1) {
         case DOWN:
            if (Config.isShaders()) {
               return Shaders.blockLightLevel05;
            }

            return 0.5F;
         case UP:
            return 1.0F;
         case NORTH:
         case SOUTH:
            if (Config.isShaders()) {
               return Shaders.blockLightLevel08;
            }

            return 0.8F;
         case WEST:
         case EAST:
            if (Config.isShaders()) {
               return Shaders.blockLightLevel06;
            }

            return 0.6F;
         default:
            return 1.0F;
      }
   }

   private float[] getPositionsDiv16(Vector3f var1, Vector3f var2) {
      float[] var3 = new float[EnumFacing.values().length];
      var3[EnumFaceDirection.Constants.WEST_INDEX] = var1.x / 16.0F;
      var3[EnumFaceDirection.Constants.DOWN_INDEX] = var1.y / 16.0F;
      var3[EnumFaceDirection.Constants.NORTH_INDEX] = var1.z / 16.0F;
      var3[EnumFaceDirection.Constants.EAST_INDEX] = var2.x / 16.0F;
      var3[EnumFaceDirection.Constants.UP_INDEX] = var2.y / 16.0F;
      var3[EnumFaceDirection.Constants.SOUTH_INDEX] = var2.z / 16.0F;
      return var3;
   }

   private void fillVertexData(
      int[] var1,
      int var2,
      EnumFacing var3,
      BlockPartFace var4,
      float[] var5,
      TextureAtlasSprite var6,
      ITransformation var7,
      BlockPartRotation var8,
      boolean var9,
      boolean var10
   ) {
      EnumFacing var14 = var7.E(var3);
      int var15 = var10 ? this.getFaceShadeColor(var14) : -1;
      EnumFaceDirection.VertexInformation var16 = EnumFaceDirection.getFacing(var3).getVertexInformation(var2);
      Vector3f var17 = new Vector3f(var5[var16.xIndex], var5[var16.yIndex], var5[var16.zIndex]);
      this.rotatePart(var17, var8);
      int var18 = this.rotateVertex(var17, var3, var2, var7, var9);
      this.storeVertexData(var1, var18, var2, var17, var15, var6, var4.blockFaceUV);
   }

   private void storeVertexData(int[] var1, int var2, int var3, Vector3f var4, int var5, TextureAtlasSprite var6, BlockFaceUV var7) {
      int var8 = var1.length / 4;
      int var9 = var2 * var8;
      var1[var9] = Float.floatToRawIntBits(var4.x);
      var1[var9 + 1] = Float.floatToRawIntBits(var4.y);
      var1[var9 + 2] = Float.floatToRawIntBits(var4.z);
      var1[var9 + 3] = var5;
      var1[var9 + 4] = Float.floatToRawIntBits(var6.getInterpolatedU(var7.func_178348_a(var3)));
      var1[var9 + 4 + 1] = Float.floatToRawIntBits(var6.getInterpolatedV(var7.func_178346_b(var3)));
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void rotatePart(Vector3f var1, BlockPartRotation var2) {
      if (var2 != null) {
         Matrix4f var6 = this.getMatrixIdentity();
         Vector3f var7 = new Vector3f(0.0F, 0.0F, 0.0F);
         switch (var2.axis) {
            case X:
               Matrix4f.rotate(var2.angle * (float) (Math.PI / 180.0), new Vector3f(1.0F, 0.0F, 0.0F), var6, var6);
               var7.set(0.0F, 1.0F, 1.0F);
               break;
            case Y:
               Matrix4f.rotate(var2.angle * (float) (Math.PI / 180.0), new Vector3f(0.0F, 1.0F, 0.0F), var6, var6);
               var7.set(1.0F, 0.0F, 1.0F);
               break;
            case Z:
               Matrix4f.rotate(var2.angle * (float) (Math.PI / 180.0), new Vector3f(0.0F, 0.0F, 1.0F), var6, var6);
               var7.set(1.0F, 1.0F, 0.0F);
         }

         if (var2.rescale) {
            if (Math.abs(var2.angle) == 22.5F) {
               var7.scale(SCALE_ROTATION_22_5);
            } else {
               var7.scale(SCALE_ROTATION_GENERAL);
            }

            Vector3f.add(var7, new Vector3f(1.0F, 1.0F, 1.0F), var7);
         } else {
            var7.set(1.0F, 1.0F, 1.0F);
         }

         this.rotateScale(var1, new Vector3f(var2.origin), var6, var7);
      }
   }

   public int rotateVertex(Vector3f var1, EnumFacing var2, int var3, ModelRotation var4, boolean var5) {
      return this.rotateVertex(var1, var2, var3, var4, var5);
   }

   public int rotateVertex(Vector3f var1, EnumFacing var2, int var3, ITransformation var4, boolean var5) {
      if (var4 == ModelRotation.X0_Y0) {
         return var3;
      }

      if (Reflector.ForgeHooksClient_transform.exists()) {
         Reflector.call(Reflector.ForgeHooksClient_transform, var1, var4.m());
      } else {
         this.rotateScale(var1, new Vector3f(0.5F, 0.5F, 0.5F), ((ModelRotation)var4).getMatrix4d(), new Vector3f(1.0F, 1.0F, 1.0F));
      }

      return var4.x(var2, var3);
   }

   private void rotateScale(Vector3f var1, Vector3f var2, Matrix4f var3, Vector3f var4) {
      Vector4f var5 = new Vector4f(var1.x - var2.x, var1.y - var2.y, var1.z - var2.z, 1.0F);
      Matrix4f.transform(var3, var5, var5);
      var5.x = var5.x * var4.x;
      var5.y = var5.y * var4.y;
      var5.z = var5.z * var4.z;
      var1.set(var5.x + var2.x, var5.y + var2.y, var5.z + var2.z);
   }

   private Matrix4f getMatrixIdentity() {
      Matrix4f var1 = new Matrix4f();
      var1.setIdentity();
      return var1;
   }

   public static EnumFacing getFacingFromVertexData(int[] var0) {
      int var5 = var0.length / 4;
      int var6 = var5 * 2;
      int var7 = var5 * 3;
      Vector3f var8 = new Vector3f(Float.intBitsToFloat(var0[0]), Float.intBitsToFloat(var0[1]), Float.intBitsToFloat(var0[2]));
      Vector3f var9 = new Vector3f(Float.intBitsToFloat(var0[var5]), Float.intBitsToFloat(var0[var5 + 1]), Float.intBitsToFloat(var0[var5 + 2]));
      Vector3f var10 = new Vector3f(Float.intBitsToFloat(var0[var6]), Float.intBitsToFloat(var0[var6 + 1]), Float.intBitsToFloat(var0[var6 + 2]));
      Vector3f var11 = new Vector3f();
      Vector3f var12 = new Vector3f();
      Vector3f var13 = new Vector3f();
      Vector3f.sub(var8, var9, var11);
      Vector3f.sub(var10, var9, var12);
      Vector3f.cross(var12, var11, var13);
      float var14 = (float)Math.sqrt(var13.x * var13.x + var13.y * var13.y + var13.z * var13.z);
      var13.x /= var14;
      var13.y /= var14;
      var13.z /= var14;
      EnumFacing var15 = null;
      float var16 = 0.0F;

      for (EnumFacing var20 : EnumFacing.values()) {
         Vec3i var21 = var20.getDirectionVec();
         Vector3f var22 = new Vector3f(var21.getX(), var21.getY(), var21.getZ());
         float var23 = Vector3f.dot(var13, var22);
         if (var23 >= 0.0F && var23 > var16) {
            var16 = var23;
            var15 = var20;
         }
      }

      if (var16 < 0.719F) {
         if (var15 != EnumFacing.EAST && var15 != EnumFacing.WEST && var15 != EnumFacing.NORTH && var15 != EnumFacing.SOUTH) {
            var15 = EnumFacing.UP;
         } else {
            var15 = EnumFacing.NORTH;
         }
      }

      return var15 == null ? EnumFacing.UP : var15;
   }

   public void lockUv(int[] var1, EnumFacing var2, BlockFaceUV var3, TextureAtlasSprite var4) {
      for (int var8 = 0; var8 < 4; var8++) {
         this.lockVertexUv(var8, var1, var2, var3, var4);
      }
   }

   private void applyFacing(int[] var1, EnumFacing var2) {
      int[] var6 = new int[var1.length];
      System.arraycopy(var1, 0, var6, 0, var1.length);
      float[] var7 = new float[EnumFacing.values().length];
      var7[EnumFaceDirection.Constants.WEST_INDEX] = 999.0F;
      var7[EnumFaceDirection.Constants.DOWN_INDEX] = 999.0F;
      var7[EnumFaceDirection.Constants.NORTH_INDEX] = 999.0F;
      var7[EnumFaceDirection.Constants.EAST_INDEX] = -999.0F;
      var7[EnumFaceDirection.Constants.UP_INDEX] = -999.0F;
      var7[EnumFaceDirection.Constants.SOUTH_INDEX] = -999.0F;
      int var8 = var1.length / 4;

      for (int var9 = 0; var9 < 4; var9++) {
         int var10 = var8 * var9;
         float var11 = Float.intBitsToFloat(var6[var10]);
         float var12 = Float.intBitsToFloat(var6[var10 + 1]);
         float var13 = Float.intBitsToFloat(var6[var10 + 2]);
         if (var11 < var7[EnumFaceDirection.Constants.WEST_INDEX]) {
            var7[EnumFaceDirection.Constants.WEST_INDEX] = var11;
         }

         if (var12 < var7[EnumFaceDirection.Constants.DOWN_INDEX]) {
            var7[EnumFaceDirection.Constants.DOWN_INDEX] = var12;
         }

         if (var13 < var7[EnumFaceDirection.Constants.NORTH_INDEX]) {
            var7[EnumFaceDirection.Constants.NORTH_INDEX] = var13;
         }

         if (var11 > var7[EnumFaceDirection.Constants.EAST_INDEX]) {
            var7[EnumFaceDirection.Constants.EAST_INDEX] = var11;
         }

         if (var12 > var7[EnumFaceDirection.Constants.UP_INDEX]) {
            var7[EnumFaceDirection.Constants.UP_INDEX] = var12;
         }

         if (var13 > var7[EnumFaceDirection.Constants.SOUTH_INDEX]) {
            var7[EnumFaceDirection.Constants.SOUTH_INDEX] = var13;
         }
      }

      EnumFaceDirection var21 = EnumFaceDirection.getFacing(var2);

      for (int var22 = 0; var22 < 4; var22++) {
         int var23 = var8 * var22;
         EnumFaceDirection.VertexInformation var24 = var21.getVertexInformation(var22);
         float var25 = var7[var24.xIndex];
         float var14 = var7[var24.yIndex];
         float var15 = var7[var24.zIndex];
         var1[var23] = Float.floatToRawIntBits(var25);
         var1[var23 + 1] = Float.floatToRawIntBits(var14);
         var1[var23 + 2] = Float.floatToRawIntBits(var15);

         for (int var16 = 0; var16 < 4; var16++) {
            int var17 = var8 * var16;
            float var18 = Float.intBitsToFloat(var6[var17]);
            float var19 = Float.intBitsToFloat(var6[var17 + 1]);
            float var20 = Float.intBitsToFloat(var6[var17 + 2]);
            if (MathHelper.epsilonEquals(var25, var18) && MathHelper.epsilonEquals(var14, var19) && MathHelper.epsilonEquals(var15, var20)) {
               var1[var23 + 4] = var6[var17 + 4];
               var1[var23 + 4 + 1] = var6[var17 + 4 + 1];
            }
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void lockVertexUv(int var1, int[] var2, EnumFacing var3, BlockFaceUV var4, TextureAtlasSprite var5) {
      int var9 = var2.length / 4;
      int var10 = var9 * var1;
      float var11 = Float.intBitsToFloat(var2[var10]);
      float var12 = Float.intBitsToFloat(var2[var10 + 1]);
      float var13 = Float.intBitsToFloat(var2[var10 + 2]);
      if (var11 < -0.1F || var11 >= 1.1F) {
         var11 -= MathHelper.floor_float(var11);
      }

      if (var12 < -0.1F || var12 >= 1.1F) {
         var12 -= MathHelper.floor_float(var12);
      }

      if (var13 < -0.1F || var13 >= 1.1F) {
         var13 -= MathHelper.floor_float(var13);
      }

      float var14 = 0.0F;
      float var15 = 0.0F;
      switch (var3) {
         case DOWN:
            var14 = var11 * 16.0F;
            var15 = (1.0F - var13) * 16.0F;
            break;
         case UP:
            var14 = var11 * 16.0F;
            var15 = var13 * 16.0F;
            break;
         case NORTH:
            var14 = (1.0F - var11) * 16.0F;
            var15 = (1.0F - var12) * 16.0F;
            break;
         case SOUTH:
            var14 = var11 * 16.0F;
            var15 = (1.0F - var12) * 16.0F;
            break;
         case WEST:
            var14 = var13 * 16.0F;
            var15 = (1.0F - var12) * 16.0F;
            break;
         case EAST:
            var14 = (1.0F - var13) * 16.0F;
            var15 = (1.0F - var12) * 16.0F;
      }

      int var16 = var4.func_178345_c(var1) * var9;
      var2[var16 + 4] = Float.floatToRawIntBits(var5.getInterpolatedU(var14));
      var2[var16 + 4 + 1] = Float.floatToRawIntBits(var5.getInterpolatedV(var15));
   }
}
