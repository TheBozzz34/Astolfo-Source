package net.minecraft.client.shader;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

public class ShaderUniform {
   private static final Logger logger = LogManager.getLogger();
   private int uniformLocation;
   private final int uniformCount;
   private final int uniformType;
   private final IntBuffer uniformIntBuffer;
   private final FloatBuffer uniformFloatBuffer;
   private final String shaderName;
   private boolean dirty;
   private final ShaderManager shaderManager;

   public ShaderUniform(String name, int typeIn, int count, ShaderManager manager) {
      this.shaderName = name;
      this.uniformCount = count;
      this.uniformType = typeIn;
      this.shaderManager = manager;
      if (typeIn <= 3) {
         this.uniformIntBuffer = BufferUtils.createIntBuffer(count);
         this.uniformFloatBuffer = null;
      } else {
         this.uniformIntBuffer = null;
         this.uniformFloatBuffer = BufferUtils.createFloatBuffer(count);
      }

      this.uniformLocation = -1;
      this.markDirty();
   }

   private void markDirty() {
      this.dirty = true;
      if (this.shaderManager != null) {
         this.shaderManager.markDirty();
      }
   }

   public static int parseType(String string) {
      byte b = -1;
      if (string.equals("int")) {
         b = 0;
      } else if (string.equals("float")) {
         b = 4;
      } else if (string.startsWith("matrix")) {
         if (string.endsWith("2x2")) {
            b = 8;
         } else if (string.endsWith("3x3")) {
            b = 9;
         } else if (string.endsWith("4x4")) {
            b = 10;
         }
      }

      return b;
   }

   public void setUniformLocation(int i) {
      this.uniformLocation = i;
   }

   public String getShaderName() {
      return this.shaderName;
   }

   public void set(float f) {
      ((Buffer)this.uniformFloatBuffer).position(0);
      this.uniformFloatBuffer.put(0, f);
      this.markDirty();
   }

   public void set(float f, float g) {
      ((Buffer)this.uniformFloatBuffer).position(0);
      this.uniformFloatBuffer.put(0, f);
      this.uniformFloatBuffer.put(1, g);
      this.markDirty();
   }

   public void set(float f, float g, float h) {
      ((Buffer)this.uniformFloatBuffer).position(0);
      this.uniformFloatBuffer.put(0, f);
      this.uniformFloatBuffer.put(1, g);
      this.uniformFloatBuffer.put(2, h);
      this.markDirty();
   }

   public void set(float f, float g, float h, float f2) {
      ((Buffer)this.uniformFloatBuffer).position(0);
      this.uniformFloatBuffer.put(f);
      this.uniformFloatBuffer.put(g);
      this.uniformFloatBuffer.put(h);
      this.uniformFloatBuffer.put(f2);
      ((Buffer)this.uniformFloatBuffer).flip();
      this.markDirty();
   }

   public void func_148092_b(float f, float g, float h, float f2) {
      ((Buffer)this.uniformFloatBuffer).position(0);
      if (this.uniformType >= 4) {
         this.uniformFloatBuffer.put(0, f);
      }

      if (this.uniformType >= 5) {
         this.uniformFloatBuffer.put(1, g);
      }

      if (this.uniformType >= 6) {
         this.uniformFloatBuffer.put(2, h);
      }

      if (this.uniformType >= 7) {
         this.uniformFloatBuffer.put(3, f2);
      }

      this.markDirty();
   }

   public void set(int i, int j, int k, int l) {
      ((Buffer)this.uniformIntBuffer).position(0);
      if (this.uniformType >= 0) {
         this.uniformIntBuffer.put(0, i);
      }

      if (this.uniformType >= 1) {
         this.uniformIntBuffer.put(1, j);
      }

      if (this.uniformType >= 2) {
         this.uniformIntBuffer.put(2, k);
      }

      if (this.uniformType >= 3) {
         this.uniformIntBuffer.put(3, l);
      }

      this.markDirty();
   }

   public void set(float[] floats) {
      if (floats.length < this.uniformCount) {
         logger.warn("Uniform.set called with a too-small value array (expected " + this.uniformCount + ", got " + floats.length + "). Ignoring.");
      } else {
         ((Buffer)this.uniformFloatBuffer).position(0);
         this.uniformFloatBuffer.put(floats);
         ((Buffer)this.uniformFloatBuffer).position(0);
         this.markDirty();
      }
   }

   public void set(
      float f,
      float g,
      float h,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      float f10,
      float f11,
      float f12,
      float f13,
      float f14
   ) {
      ((Buffer)this.uniformFloatBuffer).position(0);
      this.uniformFloatBuffer.put(0, f);
      this.uniformFloatBuffer.put(1, g);
      this.uniformFloatBuffer.put(2, h);
      this.uniformFloatBuffer.put(3, f2);
      this.uniformFloatBuffer.put(4, f3);
      this.uniformFloatBuffer.put(5, f4);
      this.uniformFloatBuffer.put(6, f5);
      this.uniformFloatBuffer.put(7, f6);
      this.uniformFloatBuffer.put(8, f7);
      this.uniformFloatBuffer.put(9, f8);
      this.uniformFloatBuffer.put(10, f9);
      this.uniformFloatBuffer.put(11, f10);
      this.uniformFloatBuffer.put(12, f11);
      this.uniformFloatBuffer.put(13, f12);
      this.uniformFloatBuffer.put(14, f13);
      this.uniformFloatBuffer.put(15, f14);
      this.markDirty();
   }

   public void set(Matrix4f matrix4f) {
      this.set(
         matrix4f.m00,
         matrix4f.m01,
         matrix4f.m02,
         matrix4f.m03,
         matrix4f.m10,
         matrix4f.m11,
         matrix4f.m12,
         matrix4f.m13,
         matrix4f.m20,
         matrix4f.m21,
         matrix4f.m22,
         matrix4f.m23,
         matrix4f.m30,
         matrix4f.m31,
         matrix4f.m32,
         matrix4f.m33
      );
   }

   public void upload() {
      if (!this.dirty) {
      }

      this.dirty = false;
      if (this.uniformType <= 3) {
         this.uploadInt();
      } else if (this.uniformType <= 7) {
         this.uploadFloat();
      } else {
         if (this.uniformType > 10) {
            logger.warn("Uniform.upload called, but type value (" + this.uniformType + ") is not a valid type. Ignoring.");
            return;
         }

         this.uploadFloatMatrix();
      }
   }

   private void uploadInt() {
      switch (this.uniformType) {
         case 0:
            OpenGlHelper.glUniform1(this.uniformLocation, this.uniformIntBuffer);
            break;
         case 1:
            OpenGlHelper.glUniform2(this.uniformLocation, this.uniformIntBuffer);
            break;
         case 2:
            OpenGlHelper.glUniform3(this.uniformLocation, this.uniformIntBuffer);
            break;
         case 3:
            OpenGlHelper.glUniform4(this.uniformLocation, this.uniformIntBuffer);
            break;
         default:
            logger.warn("Uniform.upload called, but count value (" + this.uniformCount + ") is  not in the range of 1 to 4. Ignoring.");
      }
   }

   private void uploadFloat() {
      switch (this.uniformType) {
         case 4:
            OpenGlHelper.glUniform1(this.uniformLocation, this.uniformFloatBuffer);
            break;
         case 5:
            OpenGlHelper.glUniform2(this.uniformLocation, this.uniformFloatBuffer);
            break;
         case 6:
            OpenGlHelper.glUniform3(this.uniformLocation, this.uniformFloatBuffer);
            break;
         case 7:
            OpenGlHelper.glUniform4(this.uniformLocation, this.uniformFloatBuffer);
            break;
         default:
            logger.warn("Uniform.upload called, but count value (" + this.uniformCount + ") is not in the range of 1 to 4. Ignoring.");
      }
   }

   private void uploadFloatMatrix() {
      switch (this.uniformType) {
         case 8:
            OpenGlHelper.glUniformMatrix2(this.uniformLocation, true, this.uniformFloatBuffer);
            break;
         case 9:
            OpenGlHelper.glUniformMatrix3(this.uniformLocation, true, this.uniformFloatBuffer);
            break;
         case 10:
            OpenGlHelper.glUniformMatrix4(this.uniformLocation, true, this.uniformFloatBuffer);
      }
   }
}
