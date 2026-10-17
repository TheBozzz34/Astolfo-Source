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

   public BakedQuad makeBakedQuad(
      Vector3f posFrom,
      Vector3f posTo,
      BlockPartFace face,
      TextureAtlasSprite sprite,
      EnumFacing facing,
      ModelRotation modelRotationIn,
      BlockPartRotation partRotation,
      boolean uvLocked,
      boolean shade
   ) {
      int[] ints = this.makeQuadVertexData(face, sprite, facing, this.getPositionsDiv16(posFrom, posTo), modelRotationIn, partRotation, uvLocked, shade);
      EnumFacing enumFacing = getFacingFromVertexData(ints);
      if (uvLocked) {
         this.lockUv(ints, enumFacing, face.blockFaceUV, sprite);
      }

      if (partRotation == null) {
         this.applyFacing(ints, enumFacing);
      }

      return new BakedQuad(ints, face.tintIndex, enumFacing, sprite);
   }

   public BakedQuad makeBakedQuad(
      Vector3f vector3f,
      Vector3f vector3f2,
      BlockPartFace blockPartFace,
      TextureAtlasSprite textureAtlasSprite,
      EnumFacing enumFacing,
      ITransformation iTransformation,
      BlockPartRotation blockPartRotation,
      boolean bl,
      boolean bl2
   ) {
      int[] ints = this.makeQuadVertexData(
         blockPartFace, textureAtlasSprite, enumFacing, this.getPositionsDiv16(vector3f, vector3f2), iTransformation, blockPartRotation, bl, bl2
      );
      EnumFacing enumFacing2 = getFacingFromVertexData(ints);
      if (bl) {
         this.lockUv(ints, enumFacing2, blockPartFace.blockFaceUV, textureAtlasSprite);
      }

      if (blockPartRotation == null) {
         this.applyFacing(ints, enumFacing2);
      }

      if (Reflector.ForgeHooksClient_fillNormal.exists()) {
         Reflector.callVoid(Reflector.ForgeHooksClient_fillNormal, ints, enumFacing2);
      }

      return new BakedQuad(ints, blockPartFace.tintIndex, enumFacing2, textureAtlasSprite);
   }

   private int[] makeQuadVertexData(
      BlockPartFace blockPartFace,
      TextureAtlasSprite textureAtlasSprite,
      EnumFacing enumFacing,
      float[] floats,
      ITransformation iTransformation,
      BlockPartRotation blockPartRotation,
      boolean bl,
      boolean bl2
   ) {
      byte b = 28;
      if (Config.isShaders()) {
         b = 56;
      }

      int[] ints = new int[b];

      for (int i = 0; i < 4; i++) {
         this.fillVertexData(ints, i, enumFacing, blockPartFace, floats, textureAtlasSprite, iTransformation, blockPartRotation, bl, bl2);
      }

      return ints;
   }

   private int getFaceShadeColor(EnumFacing facing) {
      float f = this.getFaceBrightness(facing);
      int i = MathHelper.clamp_int((int)(f * 255.0F), 0, 255);
      return 0xFF000000 | i << 16 | i << 8 | i;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private float getFaceBrightness(EnumFacing facing) {
      switch (facing) {
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

   private float[] getPositionsDiv16(Vector3f pos1, Vector3f pos2) {
      float[] floats = new float[EnumFacing.values().length];
      floats[EnumFaceDirection.Constants.WEST_INDEX] = pos1.x / 16.0F;
      floats[EnumFaceDirection.Constants.DOWN_INDEX] = pos1.y / 16.0F;
      floats[EnumFaceDirection.Constants.NORTH_INDEX] = pos1.z / 16.0F;
      floats[EnumFaceDirection.Constants.EAST_INDEX] = pos2.x / 16.0F;
      floats[EnumFaceDirection.Constants.UP_INDEX] = pos2.y / 16.0F;
      floats[EnumFaceDirection.Constants.SOUTH_INDEX] = pos2.z / 16.0F;
      return floats;
   }

   private void fillVertexData(
      int[] ints,
      int i,
      EnumFacing enumFacing,
      BlockPartFace blockPartFace,
      float[] floats,
      TextureAtlasSprite textureAtlasSprite,
      ITransformation iTransformation,
      BlockPartRotation blockPartRotation,
      boolean bl,
      boolean bl2
   ) {
      EnumFacing enumFacing2 = iTransformation.rotate(enumFacing);
      int j = bl2 ? this.getFaceShadeColor(enumFacing2) : -1;
      EnumFaceDirection.VertexInformation vertexInformation = EnumFaceDirection.getFacing(enumFacing).getVertexInformation(i);
      Vector3f vector3f = new Vector3f(floats[vertexInformation.xIndex], floats[vertexInformation.yIndex], floats[vertexInformation.zIndex]);
      this.rotatePart(vector3f, blockPartRotation);
      int k = this.rotateVertex(vector3f, enumFacing, i, iTransformation, bl);
      this.storeVertexData(ints, k, i, vector3f, j, textureAtlasSprite, blockPartFace.blockFaceUV);
   }

   private void storeVertexData(
      int[] faceData, int storeIndex, int vertexIndex, Vector3f position, int shadeColor, TextureAtlasSprite sprite, BlockFaceUV faceUV
   ) {
      int i = faceData.length / 4;
      int j = storeIndex * i;
      faceData[j] = Float.floatToRawIntBits(position.x);
      faceData[j + 1] = Float.floatToRawIntBits(position.y);
      faceData[j + 2] = Float.floatToRawIntBits(position.z);
      faceData[j + 3] = shadeColor;
      faceData[j + 4] = Float.floatToRawIntBits(sprite.getInterpolatedU(faceUV.func_178348_a(vertexIndex)));
      faceData[j + 4 + 1] = Float.floatToRawIntBits(sprite.getInterpolatedV(faceUV.func_178346_b(vertexIndex)));
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void rotatePart(Vector3f vector3f, BlockPartRotation partRotation) {
      if (partRotation != null) {
         Matrix4f matrix4f = this.getMatrixIdentity();
         Vector3f vector3f2 = new Vector3f(0.0F, 0.0F, 0.0F);
         switch (partRotation.axis) {
            case X:
               Matrix4f.rotate(partRotation.angle * (float) (Math.PI / 180.0), new Vector3f(1.0F, 0.0F, 0.0F), matrix4f, matrix4f);
               vector3f2.set(0.0F, 1.0F, 1.0F);
               break;
            case Y:
               Matrix4f.rotate(partRotation.angle * (float) (Math.PI / 180.0), new Vector3f(0.0F, 1.0F, 0.0F), matrix4f, matrix4f);
               vector3f2.set(1.0F, 0.0F, 1.0F);
               break;
            case Z:
               Matrix4f.rotate(partRotation.angle * (float) (Math.PI / 180.0), new Vector3f(0.0F, 0.0F, 1.0F), matrix4f, matrix4f);
               vector3f2.set(1.0F, 1.0F, 0.0F);
         }

         if (partRotation.rescale) {
            if (Math.abs(partRotation.angle) == 22.5F) {
               vector3f2.scale(SCALE_ROTATION_22_5);
            } else {
               vector3f2.scale(SCALE_ROTATION_GENERAL);
            }

            Vector3f.add(vector3f2, new Vector3f(1.0F, 1.0F, 1.0F), vector3f2);
         } else {
            vector3f2.set(1.0F, 1.0F, 1.0F);
         }

         this.rotateScale(vector3f, new Vector3f(partRotation.origin), matrix4f, vector3f2);
      }
   }

   public int rotateVertex(Vector3f position, EnumFacing facing, int vertexIndex, ModelRotation modelRotationIn, boolean uvLocked) {
      return this.rotateVertex(position, facing, vertexIndex, modelRotationIn, uvLocked);
   }

   public int rotateVertex(Vector3f vector3f, EnumFacing enumFacing, int i, ITransformation iTransformation, boolean bl) {
      if (iTransformation == ModelRotation.X0_Y0) {
         return i;
      }

      if (Reflector.ForgeHooksClient_transform.exists()) {
         Reflector.call(Reflector.ForgeHooksClient_transform, vector3f, iTransformation.getMatrix());
      } else {
         this.rotateScale(vector3f, new Vector3f(0.5F, 0.5F, 0.5F), ((ModelRotation)iTransformation).getMatrix4d(), new Vector3f(1.0F, 1.0F, 1.0F));
      }

      return iTransformation.x(enumFacing, i);
   }

   private void rotateScale(Vector3f position, Vector3f rotationOrigin, Matrix4f rotationMatrix, Vector3f scale) {
      Vector4f vector4f = new Vector4f(position.x - rotationOrigin.x, position.y - rotationOrigin.y, position.z - rotationOrigin.z, 1.0F);
      Matrix4f.transform(rotationMatrix, vector4f, vector4f);
      vector4f.x = vector4f.x * scale.x;
      vector4f.y = vector4f.y * scale.y;
      vector4f.z = vector4f.z * scale.z;
      position.set(vector4f.x + rotationOrigin.x, vector4f.y + rotationOrigin.y, vector4f.z + rotationOrigin.z);
   }

   private Matrix4f getMatrixIdentity() {
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.setIdentity();
      return matrix4f;
   }

   public static EnumFacing getFacingFromVertexData(int[] faceData) {
      int i = faceData.length / 4;
      int j = i * 2;
      int k = i * 3;
      Vector3f vector3f = new Vector3f(Float.intBitsToFloat(faceData[0]), Float.intBitsToFloat(faceData[1]), Float.intBitsToFloat(faceData[2]));
      Vector3f vector3f2 = new Vector3f(Float.intBitsToFloat(faceData[i]), Float.intBitsToFloat(faceData[i + 1]), Float.intBitsToFloat(faceData[i + 2]));
      Vector3f vector3f3 = new Vector3f(Float.intBitsToFloat(faceData[j]), Float.intBitsToFloat(faceData[j + 1]), Float.intBitsToFloat(faceData[j + 2]));
      Vector3f vector3f4 = new Vector3f();
      Vector3f vector3f5 = new Vector3f();
      Vector3f vector3f6 = new Vector3f();
      Vector3f.sub(vector3f, vector3f2, vector3f4);
      Vector3f.sub(vector3f3, vector3f2, vector3f5);
      Vector3f.cross(vector3f5, vector3f4, vector3f6);
      float f = (float)Math.sqrt(vector3f6.x * vector3f6.x + vector3f6.y * vector3f6.y + vector3f6.z * vector3f6.z);
      vector3f6.x /= f;
      vector3f6.y /= f;
      vector3f6.z /= f;
      EnumFacing enumFacing = null;
      float g = 0.0F;

      for (EnumFacing enumFacing2 : EnumFacing.values()) {
         Vec3i vec3i = enumFacing2.getDirectionVec();
         Vector3f vector3f7 = new Vector3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
         float h = Vector3f.dot(vector3f6, vector3f7);
         if (h >= 0.0F && h > g) {
            g = h;
            enumFacing = enumFacing2;
         }
      }

      if (g < 0.719F) {
         if (enumFacing != EnumFacing.EAST && enumFacing != EnumFacing.WEST && enumFacing != EnumFacing.NORTH && enumFacing != EnumFacing.SOUTH) {
            enumFacing = EnumFacing.UP;
         } else {
            enumFacing = EnumFacing.NORTH;
         }
      }

      return enumFacing == null ? EnumFacing.UP : enumFacing;
   }

   public void lockUv(int[] ints, EnumFacing facing, BlockFaceUV blockFaceUV, TextureAtlasSprite textureAtlasSprite) {
      for (int i = 0; i < 4; i++) {
         this.lockVertexUv(i, ints, facing, blockFaceUV, textureAtlasSprite);
      }
   }

   private void applyFacing(int[] ints, EnumFacing enumFacing) {
      int[] ints2 = new int[ints.length];
      System.arraycopy(ints, 0, ints2, 0, ints.length);
      float[] floats = new float[EnumFacing.values().length];
      floats[EnumFaceDirection.Constants.WEST_INDEX] = 999.0F;
      floats[EnumFaceDirection.Constants.DOWN_INDEX] = 999.0F;
      floats[EnumFaceDirection.Constants.NORTH_INDEX] = 999.0F;
      floats[EnumFaceDirection.Constants.EAST_INDEX] = -999.0F;
      floats[EnumFaceDirection.Constants.UP_INDEX] = -999.0F;
      floats[EnumFaceDirection.Constants.SOUTH_INDEX] = -999.0F;
      int i = ints.length / 4;

      for (int j = 0; j < 4; j++) {
         int k = i * j;
         float f = Float.intBitsToFloat(ints2[k]);
         float g = Float.intBitsToFloat(ints2[k + 1]);
         float h = Float.intBitsToFloat(ints2[k + 2]);
         if (f < floats[EnumFaceDirection.Constants.WEST_INDEX]) {
            floats[EnumFaceDirection.Constants.WEST_INDEX] = f;
         }

         if (g < floats[EnumFaceDirection.Constants.DOWN_INDEX]) {
            floats[EnumFaceDirection.Constants.DOWN_INDEX] = g;
         }

         if (h < floats[EnumFaceDirection.Constants.NORTH_INDEX]) {
            floats[EnumFaceDirection.Constants.NORTH_INDEX] = h;
         }

         if (f > floats[EnumFaceDirection.Constants.EAST_INDEX]) {
            floats[EnumFaceDirection.Constants.EAST_INDEX] = f;
         }

         if (g > floats[EnumFaceDirection.Constants.UP_INDEX]) {
            floats[EnumFaceDirection.Constants.UP_INDEX] = g;
         }

         if (h > floats[EnumFaceDirection.Constants.SOUTH_INDEX]) {
            floats[EnumFaceDirection.Constants.SOUTH_INDEX] = h;
         }
      }

      EnumFaceDirection enumFaceDirection = EnumFaceDirection.getFacing(enumFacing);

      for (int l = 0; l < 4; l++) {
         int m = i * l;
         EnumFaceDirection.VertexInformation vertexInformation = enumFaceDirection.getVertexInformation(l);
         float f2 = floats[vertexInformation.xIndex];
         float f3 = floats[vertexInformation.yIndex];
         float f4 = floats[vertexInformation.zIndex];
         ints[m] = Float.floatToRawIntBits(f2);
         ints[m + 1] = Float.floatToRawIntBits(f3);
         ints[m + 2] = Float.floatToRawIntBits(f4);

         for (int n = 0; n < 4; n++) {
            int i2 = i * n;
            float f5 = Float.intBitsToFloat(ints2[i2]);
            float f6 = Float.intBitsToFloat(ints2[i2 + 1]);
            float f7 = Float.intBitsToFloat(ints2[i2 + 2]);
            if (MathHelper.epsilonEquals(f2, f5) && MathHelper.epsilonEquals(f3, f6) && MathHelper.epsilonEquals(f4, f7)) {
               ints[m + 4] = ints2[i2 + 4];
               ints[m + 4 + 1] = ints2[i2 + 4 + 1];
            }
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void lockVertexUv(int i, int[] ints, EnumFacing facing, BlockFaceUV blockFaceUV, TextureAtlasSprite textureAtlasSprite) {
      int j = ints.length / 4;
      int k = j * i;
      float f = Float.intBitsToFloat(ints[k]);
      float g = Float.intBitsToFloat(ints[k + 1]);
      float h = Float.intBitsToFloat(ints[k + 2]);
      if (f < -0.1F || f >= 1.1F) {
         f -= MathHelper.floor_float(f);
      }

      if (g < -0.1F || g >= 1.1F) {
         g -= MathHelper.floor_float(g);
      }

      if (h < -0.1F || h >= 1.1F) {
         h -= MathHelper.floor_float(h);
      }

      float f2 = 0.0F;
      float f3 = 0.0F;
      switch (facing) {
         case DOWN:
            f2 = f * 16.0F;
            f3 = (1.0F - h) * 16.0F;
            break;
         case UP:
            f2 = f * 16.0F;
            f3 = h * 16.0F;
            break;
         case NORTH:
            f2 = (1.0F - f) * 16.0F;
            f3 = (1.0F - g) * 16.0F;
            break;
         case SOUTH:
            f2 = f * 16.0F;
            f3 = (1.0F - g) * 16.0F;
            break;
         case WEST:
            f2 = h * 16.0F;
            f3 = (1.0F - g) * 16.0F;
            break;
         case EAST:
            f2 = (1.0F - h) * 16.0F;
            f3 = (1.0F - g) * 16.0F;
      }

      int l = blockFaceUV.func_178345_c(i) * j;
      ints[l + 4] = Float.floatToRawIntBits(textureAtlasSprite.getInterpolatedU(f2));
      ints[l + 4 + 1] = Float.floatToRawIntBits(textureAtlasSprite.getInterpolatedV(f3));
   }
}
