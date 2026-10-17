package shadersmod.client;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class SVertexBuilder {
   public int vertexSize;
   public int offsetNormal;
   public int offsetUV;
   public int offsetUVCenter;
   public boolean hasNormal;
   public boolean hasTangent;
   public boolean hasUV;
   public boolean hasUVCenter;
   public long[] entityData = new long[10];
   public int entityDataIndex = 0;

   public SVertexBuilder() {
      this.entityData[this.entityDataIndex] = 0L;
   }

   public static void initVertexBuilder(WorldRenderer wrr) {
      wrr.sVertexBuilder = new SVertexBuilder();
   }

   public void pushEntity(long data) {
      this.entityDataIndex++;
      this.entityData[this.entityDataIndex] = data;
   }

   public void popEntity() {
      this.entityData[this.entityDataIndex] = 0L;
      this.entityDataIndex--;
   }

   public static void pushEntity(IBlockState blockState, BlockPos blockPos, IBlockAccess blockAccess, WorldRenderer wrr) {
      Block block = blockState.getBlock();
      int i;
      int j;
      if (blockState instanceof BlockStateBase) {
         BlockStateBase blockStateBase = (BlockStateBase)blockState;
         i = blockStateBase.getBlockId();
         j = blockStateBase.getMetadata();
      } else {
         i = Block.getIdFromBlock(block);
         j = block.getMetaFromState(blockState);
      }

      i = BlockAliases.getMappedBlockId(i, j);
      int k = block.getRenderType();
      int l = ((k & 65535) << 16) + (i & 65535);
      int m = j & 65535;
      wrr.sVertexBuilder.pushEntity(((long)m << 32) + l);
   }

   public static void popEntity(WorldRenderer wrr) {
      wrr.sVertexBuilder.popEntity();
   }

   public static boolean popEntity(boolean value, WorldRenderer wrr) {
      wrr.sVertexBuilder.popEntity();
      return value;
   }

   public static void endSetVertexFormat(WorldRenderer wrr) {
      SVertexBuilder sVertexBuilder2 = wrr.sVertexBuilder;
      VertexFormat vertexFormat = wrr.getVertexFormat();
      sVertexBuilder2.vertexSize = vertexFormat.getNextOffset() / 4;
      sVertexBuilder2.hasNormal = vertexFormat.hasNormal();
      sVertexBuilder2.hasTangent = sVertexBuilder2.hasNormal;
      sVertexBuilder2.hasUV = vertexFormat.hasUvOffset(0);
      sVertexBuilder2.offsetNormal = sVertexBuilder2.hasNormal ? vertexFormat.getNormalOffset() / 4 : 0;
      sVertexBuilder2.offsetUV = sVertexBuilder2.hasUV ? vertexFormat.getUvOffsetById(0) / 4 : 0;
      sVertexBuilder2.offsetUVCenter = 8;
   }

   public static void beginAddVertex(WorldRenderer wrr) {
      if (wrr.vertexCount == 0) {
         endSetVertexFormat(wrr);
      }
   }

   public static void endAddVertex(WorldRenderer wrr) {
      SVertexBuilder sVertexBuilder2 = wrr.sVertexBuilder;
      if (sVertexBuilder2.vertexSize == 14) {
         if (wrr.drawMode == 7 && wrr.vertexCount % 4 == 0) {
            sVertexBuilder2.calcNormal(wrr, wrr.getBufferSize() - 4 * sVertexBuilder2.vertexSize);
         }

         long l = sVertexBuilder2.entityData[sVertexBuilder2.entityDataIndex];
         int i = wrr.getBufferSize() - 14 + 12;
         wrr.rawIntBuffer.put(i, (int)l);
         wrr.rawIntBuffer.put(i + 1, (int)(l >> 32));
      }
   }

   public static void beginAddVertexData(WorldRenderer wrr, int[] data) {
      if (wrr.vertexCount == 0) {
         endSetVertexFormat(wrr);
      }

      SVertexBuilder sVertexBuilder2 = wrr.sVertexBuilder;
      if (sVertexBuilder2.vertexSize == 14) {
         long l = sVertexBuilder2.entityData[sVertexBuilder2.entityDataIndex];

         for (int i = 12; i + 1 < data.length; i += 14) {
            data[i] = (int)l;
            data[i + 1] = (int)(l >> 32);
         }
      }
   }

   public static void endAddVertexData(WorldRenderer wrr) {
      SVertexBuilder sVertexBuilder2 = wrr.sVertexBuilder;
      if (sVertexBuilder2.vertexSize == 14 && wrr.drawMode == 7 && wrr.vertexCount % 4 == 0) {
         sVertexBuilder2.calcNormal(wrr, wrr.getBufferSize() - 4 * sVertexBuilder2.vertexSize);
      }
   }

   public void calcNormal(WorldRenderer wrr, int baseIndex) {
      FloatBuffer floatBuffer = wrr.rawFloatBuffer;
      IntBuffer intBuffer = wrr.rawIntBuffer;
      int i = wrr.getBufferSize();
      float f = floatBuffer.get(baseIndex + 0 * this.vertexSize);
      float g = floatBuffer.get(baseIndex + 0 * this.vertexSize + 1);
      float h = floatBuffer.get(baseIndex + 0 * this.vertexSize + 2);
      float f2 = floatBuffer.get(baseIndex + 0 * this.vertexSize + this.offsetUV);
      float f3 = floatBuffer.get(baseIndex + 0 * this.vertexSize + this.offsetUV + 1);
      float f4 = floatBuffer.get(baseIndex + 1 * this.vertexSize);
      float f5 = floatBuffer.get(baseIndex + 1 * this.vertexSize + 1);
      float f6 = floatBuffer.get(baseIndex + 1 * this.vertexSize + 2);
      float f7 = floatBuffer.get(baseIndex + 1 * this.vertexSize + this.offsetUV);
      float f8 = floatBuffer.get(baseIndex + 1 * this.vertexSize + this.offsetUV + 1);
      float f9 = floatBuffer.get(baseIndex + 2 * this.vertexSize);
      float f10 = floatBuffer.get(baseIndex + 2 * this.vertexSize + 1);
      float f11 = floatBuffer.get(baseIndex + 2 * this.vertexSize + 2);
      float f12 = floatBuffer.get(baseIndex + 2 * this.vertexSize + this.offsetUV);
      float f13 = floatBuffer.get(baseIndex + 2 * this.vertexSize + this.offsetUV + 1);
      float f14 = floatBuffer.get(baseIndex + 3 * this.vertexSize);
      float f15 = floatBuffer.get(baseIndex + 3 * this.vertexSize + 1);
      float f16 = floatBuffer.get(baseIndex + 3 * this.vertexSize + 2);
      float f17 = floatBuffer.get(baseIndex + 3 * this.vertexSize + this.offsetUV);
      float f18 = floatBuffer.get(baseIndex + 3 * this.vertexSize + this.offsetUV + 1);
      float f19 = f9 - f;
      float f20 = f10 - g;
      float f21 = f11 - h;
      float f22 = f14 - f4;
      float f23 = f15 - f5;
      float f24 = f16 - f6;
      float f25 = f20 * f24 - f23 * f21;
      float f26 = f21 * f22 - f24 * f19;
      float f27 = f19 * f23 - f22 * f20;
      float f28 = f25 * f25 + f26 * f26 + f27 * f27;
      float f29 = f28 != 0.0 ? (float)(1.0 / Math.sqrt(f28)) : 1.0F;
      f25 *= f29;
      f26 *= f29;
      f27 *= f29;
      f19 = f4 - f;
      f20 = f5 - g;
      f21 = f6 - h;
      float f30 = f7 - f2;
      float f31 = f8 - f3;
      f22 = f9 - f;
      f23 = f10 - g;
      f24 = f11 - h;
      float f32 = f12 - f2;
      float f33 = f13 - f3;
      float f34 = f30 * f33 - f32 * f31;
      float f35 = f34 != 0.0F ? 1.0F / f34 : 1.0F;
      float f36 = (f33 * f19 - f31 * f22) * f35;
      float f37 = (f33 * f20 - f31 * f23) * f35;
      float f38 = (f33 * f21 - f31 * f24) * f35;
      float f39 = (f30 * f22 - f32 * f19) * f35;
      float f40 = (f30 * f23 - f32 * f20) * f35;
      float f41 = (f30 * f24 - f32 * f21) * f35;
      f28 = f36 * f36 + f37 * f37 + f38 * f38;
      f29 = f28 != 0.0 ? (float)(1.0 / Math.sqrt(f28)) : 1.0F;
      f36 *= f29;
      f37 *= f29;
      f38 *= f29;
      f28 = f39 * f39 + f40 * f40 + f41 * f41;
      f29 = f28 != 0.0 ? (float)(1.0 / Math.sqrt(f28)) : 1.0F;
      f39 *= f29;
      f40 *= f29;
      f41 *= f29;
      float f42 = f27 * f37 - f26 * f38;
      float f43 = f25 * f38 - f27 * f36;
      float f44 = f26 * f36 - f25 * f37;
      float f45 = f39 * f42 + f40 * f43 + f41 * f44 < 0.0F ? -1.0F : 1.0F;
      int j = (int)(f25 * 127.0F) & 0xFF;
      int k = (int)(f26 * 127.0F) & 0xFF;
      int l = (int)(f27 * 127.0F) & 0xFF;
      int m = (l << 16) + (k << 8) + j;
      intBuffer.put(baseIndex + 0 * this.vertexSize + this.offsetNormal, m);
      intBuffer.put(baseIndex + 1 * this.vertexSize + this.offsetNormal, m);
      intBuffer.put(baseIndex + 2 * this.vertexSize + this.offsetNormal, m);
      intBuffer.put(baseIndex + 3 * this.vertexSize + this.offsetNormal, m);
      int n = ((int)(f36 * 32767.0F) & 65535) + (((int)(f37 * 32767.0F) & 65535) << 16);
      int i2 = ((int)(f38 * 32767.0F) & 65535) + (((int)(f45 * 32767.0F) & 65535) << 16);
      intBuffer.put(baseIndex + 0 * this.vertexSize + 10, n);
      intBuffer.put(baseIndex + 0 * this.vertexSize + 10 + 1, i2);
      intBuffer.put(baseIndex + 1 * this.vertexSize + 10, n);
      intBuffer.put(baseIndex + 1 * this.vertexSize + 10 + 1, i2);
      intBuffer.put(baseIndex + 2 * this.vertexSize + 10, n);
      intBuffer.put(baseIndex + 2 * this.vertexSize + 10 + 1, i2);
      intBuffer.put(baseIndex + 3 * this.vertexSize + 10, n);
      intBuffer.put(baseIndex + 3 * this.vertexSize + 10 + 1, i2);
      float f46 = (f2 + f7 + f12 + f17) / 4.0F;
      float f47 = (f3 + f8 + f13 + f18) / 4.0F;
      floatBuffer.put(baseIndex + 0 * this.vertexSize + 8, f46);
      floatBuffer.put(baseIndex + 0 * this.vertexSize + 8 + 1, f47);
      floatBuffer.put(baseIndex + 1 * this.vertexSize + 8, f46);
      floatBuffer.put(baseIndex + 1 * this.vertexSize + 8 + 1, f47);
      floatBuffer.put(baseIndex + 2 * this.vertexSize + 8, f46);
      floatBuffer.put(baseIndex + 2 * this.vertexSize + 8 + 1, f47);
      floatBuffer.put(baseIndex + 3 * this.vertexSize + 8, f46);
      floatBuffer.put(baseIndex + 3 * this.vertexSize + 8 + 1, f47);
   }

   public static void calcNormalChunkLayer(WorldRenderer wrr) {
      if (wrr.getVertexFormat().hasNormal() && wrr.drawMode == 7 && wrr.vertexCount % 4 == 0) {
         SVertexBuilder sVertexBuilder2 = wrr.sVertexBuilder;
         endSetVertexFormat(wrr);
         int i = wrr.vertexCount * sVertexBuilder2.vertexSize;

         for (int j = 0; j < i; j += sVertexBuilder2.vertexSize * 4) {
            sVertexBuilder2.calcNormal(wrr, j);
         }
      }
   }

   public static void drawArrays(int drawMode, int first, int count, WorldRenderer wrr) {
      if (count != 0) {
         VertexFormat vertexFormat = wrr.getVertexFormat();
         int i = vertexFormat.getNextOffset();
         if (i == 56) {
            ByteBuffer byteBuffer = wrr.getByteBuffer();
            ((Buffer)byteBuffer).position(32);
            GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, 5126, false, i, byteBuffer);
            ((Buffer)byteBuffer).position(40);
            GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, 5122, false, i, byteBuffer);
            ((Buffer)byteBuffer).position(48);
            GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, 5122, false, i, byteBuffer);
            ((Buffer)byteBuffer).position(0);
            GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
            GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
            GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
            GL11.glDrawArrays(drawMode, first, count);
            GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
            GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
            GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
         } else {
            GL11.glDrawArrays(drawMode, first, count);
         }
      }
   }
}
