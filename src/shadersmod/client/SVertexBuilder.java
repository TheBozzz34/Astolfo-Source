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
   private static final long a = 106842652122376L;

   public SVertexBuilder() {
      this.entityData[this.entityDataIndex] = 0L;
   }

   public static void initVertexBuilder(WorldRenderer var0) {
      var0.sVertexBuilder = new SVertexBuilder();
   }

   public void pushEntity(long var1) {
      this.entityDataIndex++;
      this.entityData[this.entityDataIndex] = var1;
   }

   public void popEntity() {
      this.entityData[this.entityDataIndex] = 0L;
      this.entityDataIndex--;
   }

   public static void pushEntity(IBlockState var0, BlockPos var1, IBlockAccess var2, WorldRenderer var3) {
      Block var7 = var0.getBlock();
      int var8;
      int var9;
      if (var0 instanceof BlockStateBase) {
         BlockStateBase var10 = (BlockStateBase)var0;
         var8 = var10.getBlockId();
         var9 = var10.getMetadata();
      } else {
         var8 = Block.getIdFromBlock(var7);
         var9 = var7.getMetaFromState(var0);
      }

      var8 = BlockAliases.getMappedBlockId(var8, var9);
      int var14 = var7.getRenderType();
      int var11 = ((var14 & 65535) << 16) + (var8 & 65535);
      int var12 = var9 & 65535;
      var3.sVertexBuilder.pushEntity(((long)var12 << 32) + var11);
   }

   public static void s(WorldRenderer var0) {
      var0.sVertexBuilder.popEntity();
   }

   public static boolean popEntity(boolean var0, WorldRenderer var1) {
      var1.sVertexBuilder.popEntity();
      return var0;
   }

   public static void endSetVertexFormat(WorldRenderer var0) {
      SVertexBuilder var4 = var0.sVertexBuilder;
      VertexFormat var5 = var0.getVertexFormat();
      var4.vertexSize = var5.getNextOffset() / 4;
      var4.hasNormal = var5.hasNormal();
      var4.hasTangent = var4.hasNormal;
      var4.hasUV = var5.hasUvOffset(0);
      var4.offsetNormal = var4.hasNormal ? var5.getNormalOffset() / 4 : 0;
      var4.offsetUV = var4.hasUV ? var5.getUvOffsetById(0) / 4 : 0;
      var4.offsetUVCenter = 8;
   }

   public static void beginAddVertex(WorldRenderer var0) {
      if (var0.vertexCount == 0) {
         endSetVertexFormat(var0);
      }
   }

   public static void endAddVertex(WorldRenderer var0) {
      SVertexBuilder var4 = var0.sVertexBuilder;
      if (var4.vertexSize == 14) {
         if (var0.drawMode == 7 && var0.vertexCount % 4 == 0) {
            var4.calcNormal(var0, var0.getBufferSize() - 4 * var4.vertexSize);
         }

         long var5 = var4.entityData[var4.entityDataIndex];
         int var7 = var0.getBufferSize() - 14 + 12;
         var0.rawIntBuffer.put(var7, (int)var5);
         var0.rawIntBuffer.put(var7 + 1, (int)(var5 >> 32));
      }
   }

   public static void beginAddVertexData(WorldRenderer var0, int[] var1) {
      if (var0.vertexCount == 0) {
         endSetVertexFormat(var0);
      }

      SVertexBuilder var5 = var0.sVertexBuilder;
      if (var5.vertexSize == 14) {
         long var6 = var5.entityData[var5.entityDataIndex];

         for (int var8 = 12; var8 + 1 < var1.length; var8 += 14) {
            var1[var8] = (int)var6;
            var1[var8 + 1] = (int)(var6 >> 32);
         }
      }
   }

   public static void endAddVertexData(WorldRenderer var0) {
      SVertexBuilder var4 = var0.sVertexBuilder;
      if (var4.vertexSize == 14 && var0.drawMode == 7 && var0.vertexCount % 4 == 0) {
         var4.calcNormal(var0, var0.getBufferSize() - 4 * var4.vertexSize);
      }
   }

   public void calcNormal(WorldRenderer var1, int var2) {
      FloatBuffer var6 = var1.rawFloatBuffer;
      IntBuffer var7 = var1.rawIntBuffer;
      int var8 = var1.getBufferSize();
      float var9 = var6.get(var2 + 0 * this.vertexSize);
      float var10 = var6.get(var2 + 0 * this.vertexSize + 1);
      float var11 = var6.get(var2 + 0 * this.vertexSize + 2);
      float var12 = var6.get(var2 + 0 * this.vertexSize + this.offsetUV);
      float var13 = var6.get(var2 + 0 * this.vertexSize + this.offsetUV + 1);
      float var14 = var6.get(var2 + 1 * this.vertexSize);
      float var15 = var6.get(var2 + 1 * this.vertexSize + 1);
      float var16 = var6.get(var2 + 1 * this.vertexSize + 2);
      float var17 = var6.get(var2 + 1 * this.vertexSize + this.offsetUV);
      float var18 = var6.get(var2 + 1 * this.vertexSize + this.offsetUV + 1);
      float var19 = var6.get(var2 + 2 * this.vertexSize);
      float var20 = var6.get(var2 + 2 * this.vertexSize + 1);
      float var21 = var6.get(var2 + 2 * this.vertexSize + 2);
      float var22 = var6.get(var2 + 2 * this.vertexSize + this.offsetUV);
      float var23 = var6.get(var2 + 2 * this.vertexSize + this.offsetUV + 1);
      float var24 = var6.get(var2 + 3 * this.vertexSize);
      float var25 = var6.get(var2 + 3 * this.vertexSize + 1);
      float var26 = var6.get(var2 + 3 * this.vertexSize + 2);
      float var27 = var6.get(var2 + 3 * this.vertexSize + this.offsetUV);
      float var28 = var6.get(var2 + 3 * this.vertexSize + this.offsetUV + 1);
      float var29 = var19 - var9;
      float var30 = var20 - var10;
      float var31 = var21 - var11;
      float var32 = var24 - var14;
      float var33 = var25 - var15;
      float var34 = var26 - var16;
      float var35 = var30 * var34 - var33 * var31;
      float var36 = var31 * var32 - var34 * var29;
      float var37 = var29 * var33 - var32 * var30;
      float var38 = var35 * var35 + var36 * var36 + var37 * var37;
      float var39 = var38 != 0.0 ? (float)(1.0 / Math.sqrt(var38)) : 1.0F;
      var35 *= var39;
      var36 *= var39;
      var37 *= var39;
      var29 = var14 - var9;
      var30 = var15 - var10;
      var31 = var16 - var11;
      float var40 = var17 - var12;
      float var41 = var18 - var13;
      var32 = var19 - var9;
      var33 = var20 - var10;
      var34 = var21 - var11;
      float var42 = var22 - var12;
      float var43 = var23 - var13;
      float var44 = var40 * var43 - var42 * var41;
      float var45 = var44 != 0.0F ? 1.0F / var44 : 1.0F;
      float var46 = (var43 * var29 - var41 * var32) * var45;
      float var47 = (var43 * var30 - var41 * var33) * var45;
      float var48 = (var43 * var31 - var41 * var34) * var45;
      float var49 = (var40 * var32 - var42 * var29) * var45;
      float var50 = (var40 * var33 - var42 * var30) * var45;
      float var51 = (var40 * var34 - var42 * var31) * var45;
      var38 = var46 * var46 + var47 * var47 + var48 * var48;
      var39 = var38 != 0.0 ? (float)(1.0 / Math.sqrt(var38)) : 1.0F;
      var46 *= var39;
      var47 *= var39;
      var48 *= var39;
      var38 = var49 * var49 + var50 * var50 + var51 * var51;
      var39 = var38 != 0.0 ? (float)(1.0 / Math.sqrt(var38)) : 1.0F;
      var49 *= var39;
      var50 *= var39;
      var51 *= var39;
      float var52 = var37 * var47 - var36 * var48;
      float var53 = var35 * var48 - var37 * var46;
      float var54 = var36 * var46 - var35 * var47;
      float var55 = var49 * var52 + var50 * var53 + var51 * var54 < 0.0F ? -1.0F : 1.0F;
      int var56 = (int)(var35 * 127.0F) & 0xFF;
      int var57 = (int)(var36 * 127.0F) & 0xFF;
      int var58 = (int)(var37 * 127.0F) & 0xFF;
      int var59 = (var58 << 16) + (var57 << 8) + var56;
      var7.put(var2 + 0 * this.vertexSize + this.offsetNormal, var59);
      var7.put(var2 + 1 * this.vertexSize + this.offsetNormal, var59);
      var7.put(var2 + 2 * this.vertexSize + this.offsetNormal, var59);
      var7.put(var2 + 3 * this.vertexSize + this.offsetNormal, var59);
      int var60 = ((int)(var46 * 32767.0F) & 65535) + (((int)(var47 * 32767.0F) & 65535) << 16);
      int var61 = ((int)(var48 * 32767.0F) & 65535) + (((int)(var55 * 32767.0F) & 65535) << 16);
      var7.put(var2 + 0 * this.vertexSize + 10, var60);
      var7.put(var2 + 0 * this.vertexSize + 10 + 1, var61);
      var7.put(var2 + 1 * this.vertexSize + 10, var60);
      var7.put(var2 + 1 * this.vertexSize + 10 + 1, var61);
      var7.put(var2 + 2 * this.vertexSize + 10, var60);
      var7.put(var2 + 2 * this.vertexSize + 10 + 1, var61);
      var7.put(var2 + 3 * this.vertexSize + 10, var60);
      var7.put(var2 + 3 * this.vertexSize + 10 + 1, var61);
      float var62 = (var12 + var17 + var22 + var27) / 4.0F;
      float var63 = (var13 + var18 + var23 + var28) / 4.0F;
      var6.put(var2 + 0 * this.vertexSize + 8, var62);
      var6.put(var2 + 0 * this.vertexSize + 8 + 1, var63);
      var6.put(var2 + 1 * this.vertexSize + 8, var62);
      var6.put(var2 + 1 * this.vertexSize + 8 + 1, var63);
      var6.put(var2 + 2 * this.vertexSize + 8, var62);
      var6.put(var2 + 2 * this.vertexSize + 8 + 1, var63);
      var6.put(var2 + 3 * this.vertexSize + 8, var62);
      var6.put(var2 + 3 * this.vertexSize + 8 + 1, var63);
   }

   public static void calcNormalChunkLayer(WorldRenderer var0) {
      if (var0.getVertexFormat().hasNormal() && var0.drawMode == 7 && var0.vertexCount % 4 == 0) {
         SVertexBuilder var4 = var0.sVertexBuilder;
         endSetVertexFormat(var0);
         int var5 = var0.vertexCount * var4.vertexSize;

         for (int var6 = 0; var6 < var5; var6 += var4.vertexSize * 4) {
            var4.calcNormal(var0, var6);
         }
      }
   }

   public static void drawArrays(int var0, int var1, int var2, WorldRenderer var3) {
      if (var2 != 0) {
         VertexFormat var7 = var3.getVertexFormat();
         int var8 = var7.getNextOffset();
         if (var8 == 56) {
            ByteBuffer var9 = var3.getByteBuffer();
            ((Buffer)var9).position(32);
            GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, 5126, false, var8, var9);
            ((Buffer)var9).position(40);
            GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, 5122, false, var8, var9);
            ((Buffer)var9).position(48);
            GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, 5122, false, var8, var9);
            ((Buffer)var9).position(0);
            GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
            GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
            GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
            GL11.glDrawArrays(var0, var1, var2);
            GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
            GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
            GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
         } else {
            GL11.glDrawArrays(var0, var1, var2);
         }
      }
   }
}
