package net.minecraft.client.renderer;

import bypass.module.ModuleManager;
import bypass.module.visuals.Xray;
import com.google.common.primitives.Floats;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.src.Config;
import net.minecraft.src.TextureUtils;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;
import shadersmod.client.SVertexBuilder;

public class WorldRenderer {
   private ByteBuffer byteBuffer;
   public IntBuffer rawIntBuffer;
   private ShortBuffer rawShortBuffer;
   public FloatBuffer rawFloatBuffer;
   public int vertexCount;
   private VertexFormatElement vertexFormatElement;
   private int vertexFormatIndex;
   private boolean noColor;
   public int drawMode;
   private double xOffset;
   private double yOffset;
   private double zOffset;
   private VertexFormat vertexFormat;
   private boolean isDrawing;
   private EnumWorldBlockLayer blockLayer = null;
   private boolean[] drawnIcons = new boolean[256];
   private TextureAtlasSprite[] quadSprites = null;
   private TextureAtlasSprite[] quadSpritesPrev = null;
   private TextureAtlasSprite quadSprite = null;
   public SVertexBuilder sVertexBuilder;
   public static boolean S;
   private static final long c = 62659924982104L;

   public WorldRenderer(int var1) {
      if (Config.isShaders()) {
         var1 *= 2;
      }

      this.byteBuffer = GLAllocation.createDirectByteBuffer(var1 * 4);
      this.rawIntBuffer = this.byteBuffer.asIntBuffer();
      this.rawShortBuffer = this.byteBuffer.asShortBuffer();
      this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
      SVertexBuilder.initVertexBuilder(this);
   }

   private void growBuffer(int var1) {
      if (Config.isShaders()) {
         var1 *= 2;
      }

      if (var1 > this.rawIntBuffer.remaining()) {
         int var5 = this.byteBuffer.capacity();
         int var6 = var5 % 2097152;
         int var7 = var6 + (((this.rawIntBuffer.position() + var1) * 4 - var6) / 2097152 + 1) * 2097152;
         LogManager.getLogger().warn("Needed to grow BufferBuilder buffer: Old size " + var5 + " bytes, new size " + var7 + " bytes.");
         int var8 = this.rawIntBuffer.position();
         ByteBuffer var9 = GLAllocation.createDirectByteBuffer(var7);
         ((Buffer)this.byteBuffer).position(0);
         var9.put(this.byteBuffer);
         ((Buffer)var9).rewind();
         this.byteBuffer = var9;
         this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
         this.rawIntBuffer = this.byteBuffer.asIntBuffer();
         ((Buffer)this.rawIntBuffer).position(var8);
         this.rawShortBuffer = this.byteBuffer.asShortBuffer();
         ((Buffer)this.rawShortBuffer).position(var8 << 1);
         if (this.quadSprites != null) {
            TextureAtlasSprite[] var10 = this.quadSprites;
            int var11 = this.getBufferQuadSize();
            this.quadSprites = new TextureAtlasSprite[var11];
            System.arraycopy(var10, 0, this.quadSprites, 0, Math.min(var10.length, this.quadSprites.length));
            this.quadSpritesPrev = null;
         }
      }
   }

   public void sortVertexData(float var1, float var2, float var3) {
      int var7 = this.vertexCount / 4;
      final float[] var8 = new float[var7];

      for (int var9 = 0; var9 < var7; var9++) {
         var8[var9] = getDistanceSq(
            this.rawFloatBuffer,
            (float)(var1 + this.xOffset),
            (float)(var2 + this.yOffset),
            (float)(var3 + this.zOffset),
            this.vertexFormat.getIntegerSize(),
            var9 * this.vertexFormat.getNextOffset()
         );
      }

      Integer[] var18 = new Integer[var7];

      for (int var10 = 0; var10 < var18.length; var10++) {
         var18[var10] = var10;
      }

      Arrays.sort(var18, new Comparator<Integer>() {
         public int compare(Integer var1, Integer var2x) {
            return Floats.compare(var8[var2x], var8[var1]);
         }
      });
      BitSet var19 = new BitSet();
      int var11 = this.vertexFormat.getNextOffset();
      int[] var12 = new int[var11];

      for (int var20 = 0; (var20 = var19.nextClearBit(var20)) < var18.length; var20++) {
         int var14 = var18[var20];
         if (var14 != var20) {
            ((Buffer)this.rawIntBuffer).limit(var14 * var11 + var11);
            ((Buffer)this.rawIntBuffer).position(var14 * var11);
            this.rawIntBuffer.get(var12);
            int var15 = var14;

            for (int var16 = var18[var14]; var15 != var20; var16 = var18[var16]) {
               ((Buffer)this.rawIntBuffer).limit(var16 * var11 + var11);
               ((Buffer)this.rawIntBuffer).position(var16 * var11);
               IntBuffer var17 = this.rawIntBuffer.slice();
               ((Buffer)this.rawIntBuffer).limit(var15 * var11 + var11);
               ((Buffer)this.rawIntBuffer).position(var15 * var11);
               this.rawIntBuffer.put(var17);
               var19.set(var15);
               var15 = var16;
            }

            ((Buffer)this.rawIntBuffer).limit(var20 * var11 + var11);
            ((Buffer)this.rawIntBuffer).position(var20 * var11);
            this.rawIntBuffer.put(var12);
         }

         var19.set(var20);
      }

      ((Buffer)this.rawIntBuffer).limit(this.rawIntBuffer.capacity());
      ((Buffer)this.rawIntBuffer).position(this.getBufferSize());
      if (this.quadSprites != null) {
         TextureAtlasSprite[] var21 = new TextureAtlasSprite[this.vertexCount / 4];
         int var22 = this.vertexFormat.getIntegerSize() / 4 * 4;

         for (int var23 = 0; var23 < var18.length; var23++) {
            int var24 = var18[var23];
            var21[var23] = this.quadSprites[var24];
         }

         System.arraycopy(var21, 0, this.quadSprites, 0, var21.length);
      }
   }

   public WorldRenderer.State getVertexState() {
      ((Buffer)this.rawIntBuffer).rewind();
      int var4 = this.getBufferSize();
      ((Buffer)this.rawIntBuffer).limit(var4);
      int[] var5 = new int[var4];
      this.rawIntBuffer.get(var5);
      ((Buffer)this.rawIntBuffer).limit(this.rawIntBuffer.capacity());
      ((Buffer)this.rawIntBuffer).position(var4);
      TextureAtlasSprite[] var6 = null;
      if (this.quadSprites != null) {
         int var7 = this.vertexCount / 4;
         var6 = new TextureAtlasSprite[var7];
         System.arraycopy(this.quadSprites, 0, var6, 0, var7);
      }

      return new WorldRenderer.State(var5, new VertexFormat(this.vertexFormat), var6);
   }

   public int getBufferSize() {
      return this.vertexCount * this.vertexFormat.getIntegerSize();
   }

   private static float getDistanceSq(FloatBuffer var0, float var1, float var2, float var3, int var4, int var5) {
      float var6 = var0.get(var5 + var4 * 0 + 0);
      float var7 = var0.get(var5 + var4 * 0 + 1);
      float var8 = var0.get(var5 + var4 * 0 + 2);
      float var9 = var0.get(var5 + var4 * 1 + 0);
      float var10 = var0.get(var5 + var4 * 1 + 1);
      float var11 = var0.get(var5 + var4 * 1 + 2);
      float var12 = var0.get(var5 + var4 * 2 + 0);
      float var13 = var0.get(var5 + var4 * 2 + 1);
      float var14 = var0.get(var5 + var4 * 2 + 2);
      float var15 = var0.get(var5 + var4 * 3 + 0);
      float var16 = var0.get(var5 + var4 * 3 + 1);
      float var17 = var0.get(var5 + var4 * 3 + 2);
      float var18 = (var6 + var9 + var12 + var15) * 0.25F - var1;
      float var19 = (var7 + var10 + var13 + var16) * 0.25F - var2;
      float var20 = (var8 + var11 + var14 + var17) * 0.25F - var3;
      return var18 * var18 + var19 * var19 + var20 * var20;
   }

   public void setVertexState(WorldRenderer.State var1) {
      ((Buffer)this.rawIntBuffer).clear();
      this.growBuffer(var1.getRawBuffer().length);
      this.rawIntBuffer.put(var1.getRawBuffer());
      this.vertexCount = var1.getVertexCount();
      this.vertexFormat = new VertexFormat(var1.getVertexFormat());
      if (var1.stateQuadSprites != null) {
         if (this.quadSprites == null) {
            this.quadSprites = this.quadSpritesPrev;
         }

         if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize()) {
            this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
         }

         TextureAtlasSprite[] var5 = var1.stateQuadSprites;
         System.arraycopy(var5, 0, this.quadSprites, 0, var5.length);
      } else {
         if (this.quadSprites != null) {
            this.quadSpritesPrev = this.quadSprites;
         }

         this.quadSprites = null;
      }
   }

   public void reset() {
      this.vertexCount = 0;
      this.vertexFormatElement = null;
      this.vertexFormatIndex = 0;
      this.quadSprite = null;
   }

   public void begin(int var1, VertexFormat var2) {
      if (this.isDrawing) {
         throw new IllegalStateException("Already building!");
      }

      this.isDrawing = true;
      this.reset();
      this.drawMode = var1;
      this.vertexFormat = var2;
      this.vertexFormatElement = var2.getElement(this.vertexFormatIndex);
      this.noColor = false;
      ((Buffer)this.byteBuffer).limit(this.byteBuffer.capacity());
      if (Config.isShaders()) {
         SVertexBuilder.endSetVertexFormat(this);
      }

      if (Config.isMultiTexture()) {
         if (this.blockLayer != null) {
            if (this.quadSprites == null) {
               this.quadSprites = this.quadSpritesPrev;
            }

            if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize()) {
               this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
            }
         }
      } else {
         if (this.quadSprites != null) {
            this.quadSpritesPrev = this.quadSprites;
         }

         this.quadSprites = null;
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public WorldRenderer tex(double var1, double var3) {
      if (this.quadSprite != null && this.quadSprites != null) {
         var1 = this.quadSprite.toSingleU((float)var1);
         var3 = this.quadSprite.toSingleV((float)var3);
         this.quadSprites[this.vertexCount / 4] = this.quadSprite;
      }

      int var8 = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(var8, (float)var1);
            this.byteBuffer.putFloat(var8 + 4, (float)var3);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(var8, (int)var1);
            this.byteBuffer.putInt(var8 + 4, (int)var3);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(var8, (short)var3);
            this.byteBuffer.putShort(var8 + 2, (short)var1);
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(var8, (byte)var3);
            this.byteBuffer.put(var8 + 1, (byte)var1);
      }

      this.nextVertexFormatIndex();
      return this;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public WorldRenderer lightmap(int var1, int var2) {
      int var6 = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(var6, var1);
            this.byteBuffer.putFloat(var6 + 4, var2);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(var6, var1);
            this.byteBuffer.putInt(var6 + 4, var2);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(var6, (short)var2);
            this.byteBuffer.putShort(var6 + 2, (short)var1);
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(var6, (byte)var2);
            this.byteBuffer.put(var6 + 1, (byte)var1);
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void putBrightness4(int var1, int var2, int var3, int var4) {
      int var5 = (this.vertexCount - 4) * this.vertexFormat.getIntegerSize() + this.vertexFormat.getUvOffsetById(1) / 4;
      int var6 = this.vertexFormat.getNextOffset() >> 2;
      this.rawIntBuffer.put(var5, var1);
      this.rawIntBuffer.put(var5 + var6, var2);
      this.rawIntBuffer.put(var5 + var6 * 2, var3);
      this.rawIntBuffer.put(var5 + var6 * 3, var4);
   }

   public void putPosition(double var1, double var3, double var5) {
      int var10 = this.vertexFormat.getIntegerSize();
      int var11 = (this.vertexCount - 4) * var10;

      for (int var12 = 0; var12 < 4; var12++) {
         int var13 = var11 + var12 * var10;
         int var14 = var13 + 1;
         int var15 = var14 + 1;
         this.rawIntBuffer.put(var13, Float.floatToRawIntBits((float)(var1 + this.xOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(var13))));
         this.rawIntBuffer.put(var14, Float.floatToRawIntBits((float)(var3 + this.yOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(var14))));
         this.rawIntBuffer.put(var15, Float.floatToRawIntBits((float)(var5 + this.zOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(var15))));
      }
   }

   public int getColorIndex(int var1) {
      return ((this.vertexCount - var1) * this.vertexFormat.getNextOffset() + this.vertexFormat.getColorOffset()) / 4;
   }

   public void putColorMultiplier(float var1, float var2, float var3, int var4) {
      int var8 = this.getColorIndex(var4);
      int var9 = -1;
      if (!this.noColor) {
         var9 = this.rawIntBuffer.get(var8);
         if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            int var10 = (int)((var9 & 0xFF) * var1);
            int var11 = (int)((var9 >> 8 & 0xFF) * var2);
            int var12 = (int)((var9 >> 16 & 0xFF) * var3);
            var9 &= -16777216;
            var9 = var9 | var12 << 16 | var11 << 8 | var10;
         } else {
            int var16 = (int)((var9 >> 24 & 0xFF) * var1);
            int var17 = (int)((var9 >> 16 & 0xFF) * var2);
            int var18 = (int)((var9 >> 8 & 0xFF) * var3);
            var9 &= 255;
            var9 = var9 | var16 << 24 | var17 << 16 | var18 << 8;
         }
      }

      if (ModuleManager.s.isEnabled() && Xray.isTranslucent()) {
         this.rawIntBuffer.put(var8, MathHelper.floor_float(Xray.opacity.getValue().intValue() * 2.3F) + 25 << 24 | var9 & 16777215);
      } else {
         this.rawIntBuffer.put(var8, var9);
      }
   }

   private void putColor(int var1, int var2) {
      int var5 = this.getColorIndex(var2);
      int var6 = var1 >> 16 & 0xFF;
      int var7 = var1 >> 8 & 0xFF;
      int var8 = var1 & 0xFF;
      int var9 = var1 >> 24 & 0xFF;
      this.putColorRGBA(var5, var6, var7, var8, var9);
   }

   public void putColorRGB_F(float var1, float var2, float var3, int var4) {
      int var7 = this.getColorIndex(var4);
      int var8 = MathHelper.clamp_int((int)(var1 * 255.0F), 0, 255);
      int var9 = MathHelper.clamp_int((int)(var2 * 255.0F), 0, 255);
      int var10 = MathHelper.clamp_int((int)(var3 * 255.0F), 0, 255);
      this.putColorRGBA(var7, var8, var9, var10, 255);
   }

   public void putColorRGBA(int var1, int var2, int var3, int var4, int var5) {
      if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
         this.rawIntBuffer.put(var1, var5 << 24 | var4 << 16 | var3 << 8 | var2);
      } else {
         this.rawIntBuffer.put(var1, var2 << 24 | var3 << 16 | var4 << 8 | var5);
      }
   }

   public void noColor() {
      this.noColor = true;
   }

   public WorldRenderer color(float var1, float var2, float var3, float var4) {
      return this.color((int)(var1 * 255.0F), (int)(var2 * 255.0F), (int)(var3 * 255.0F), (int)(var4 * 255.0F));
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public WorldRenderer color(int var1, int var2, int var3, int var4) {
      if (this.noColor) {
         return this;
      }

      int var8 = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(var8, var1 / 255.0F);
            this.byteBuffer.putFloat(var8 + 4, var2 / 255.0F);
            this.byteBuffer.putFloat(var8 + 8, var3 / 255.0F);
            this.byteBuffer.putFloat(var8 + 12, var4 / 255.0F);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putFloat(var8, var1);
            this.byteBuffer.putFloat(var8 + 4, var2);
            this.byteBuffer.putFloat(var8 + 8, var3);
            this.byteBuffer.putFloat(var8 + 12, var4);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(var8, (short)var1);
            this.byteBuffer.putShort(var8 + 2, (short)var2);
            this.byteBuffer.putShort(var8 + 4, (short)var3);
            this.byteBuffer.putShort(var8 + 6, (short)var4);
            break;
         case UBYTE:
         case BYTE:
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
               this.byteBuffer.put(var8, (byte)var1);
               this.byteBuffer.put(var8 + 1, (byte)var2);
               this.byteBuffer.put(var8 + 2, (byte)var3);
               this.byteBuffer.put(var8 + 3, (byte)var4);
            } else {
               this.byteBuffer.put(var8, (byte)var4);
               this.byteBuffer.put(var8 + 1, (byte)var3);
               this.byteBuffer.put(var8 + 2, (byte)var2);
               this.byteBuffer.put(var8 + 3, (byte)var1);
            }
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void addVertexData(int[] var1) {
      if (Config.isShaders()) {
         SVertexBuilder.beginAddVertexData(this, var1);
      }

      this.growBuffer(var1.length);
      ((Buffer)this.rawIntBuffer).position(this.getBufferSize());
      this.rawIntBuffer.put(var1);
      this.vertexCount = this.vertexCount + var1.length / this.vertexFormat.getIntegerSize();
      if (Config.isShaders()) {
         SVertexBuilder.endAddVertexData(this);
      }
   }

   public void endVertex() {
      this.vertexCount++;
      this.growBuffer(this.vertexFormat.getIntegerSize());
      this.vertexFormatIndex = 0;
      this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
      if (Config.isShaders()) {
         SVertexBuilder.endAddVertex(this);
      }
   }

   public void l(int var1) {
      int var4 = var1 >> 16 & 0xFF;
      int var5 = var1 >> 8 & 0xFF;
      int var6 = var1 & 0xFF;
      this.putPosition(var4, var5, var6);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public WorldRenderer pos(double var1, double var3, double var5) {
      if (Config.isShaders()) {
         SVertexBuilder.beginAddVertex(this);
      }

      int var10 = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(var10, (float)(var1 + this.xOffset));
            this.byteBuffer.putFloat(var10 + 4, (float)(var3 + this.yOffset));
            this.byteBuffer.putFloat(var10 + 8, (float)(var5 + this.zOffset));
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(var10, Float.floatToRawIntBits((float)(var1 + this.xOffset)));
            this.byteBuffer.putInt(var10 + 4, Float.floatToRawIntBits((float)(var3 + this.yOffset)));
            this.byteBuffer.putInt(var10 + 8, Float.floatToRawIntBits((float)(var5 + this.zOffset)));
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(var10, (short)(var1 + this.xOffset));
            this.byteBuffer.putShort(var10 + 2, (short)(var3 + this.yOffset));
            this.byteBuffer.putShort(var10 + 4, (short)(var5 + this.zOffset));
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(var10, (byte)(var1 + this.xOffset));
            this.byteBuffer.put(var10 + 1, (byte)(var3 + this.yOffset));
            this.byteBuffer.put(var10 + 2, (byte)(var5 + this.zOffset));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void putNormal(float var1, float var2, float var3) {
      int var6 = (byte)(var1 * 127.0F) & 255;
      int var7 = (byte)(var2 * 127.0F) & 255;
      int var8 = (byte)(var3 * 127.0F) & 255;
      int var9 = var6 | var7 << 8 | var8 << 16;
      int var10 = this.vertexFormat.getNextOffset() >> 2;
      int var11 = (this.vertexCount - 4) * var10 + this.vertexFormat.getNormalOffset() / 4;
      this.rawIntBuffer.put(var11, var9);
      this.rawIntBuffer.put(var11 + var10, var9);
      this.rawIntBuffer.put(var11 + var10 * 2, var9);
      this.rawIntBuffer.put(var11 + var10 * 3, var9);
   }

   private void nextVertexFormatIndex() {
      this.vertexFormatIndex++;
      this.vertexFormatIndex = this.vertexFormatIndex % this.vertexFormat.getElementCount();
      this.vertexFormatElement = this.vertexFormat.getElement(this.vertexFormatIndex);
      if (this.vertexFormatElement.getUsage() == VertexFormatElement.EnumUsage.PADDING) {
         this.nextVertexFormatIndex();
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public WorldRenderer normal(float var1, float var2, float var3) {
      int var7 = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(var7, var1);
            this.byteBuffer.putFloat(var7 + 4, var2);
            this.byteBuffer.putFloat(var7 + 8, var3);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(var7, (int)var1);
            this.byteBuffer.putInt(var7 + 4, (int)var2);
            this.byteBuffer.putInt(var7 + 8, (int)var3);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(var7, (short)((int)(var1 * 32767.0F) & 65535));
            this.byteBuffer.putShort(var7 + 2, (short)((int)(var2 * 32767.0F) & 65535));
            this.byteBuffer.putShort(var7 + 4, (short)((int)(var3 * 32767.0F) & 65535));
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(var7, (byte)((int)(var1 * 127.0F) & 0xFF));
            this.byteBuffer.put(var7 + 1, (byte)((int)(var2 * 127.0F) & 0xFF));
            this.byteBuffer.put(var7 + 2, (byte)((int)(var3 * 127.0F) & 0xFF));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void setTranslation(double var1, double var3, double var5) {
      this.xOffset = var1;
      this.yOffset = var3;
      this.zOffset = var5;
   }

   public void finishDrawing() {
      if (!this.isDrawing) {
         throw new IllegalStateException("Not building!");
      }

      this.isDrawing = false;
      ((Buffer)this.byteBuffer).position(0);
      ((Buffer)this.byteBuffer).limit(this.getBufferSize() * 4);
   }

   public ByteBuffer getByteBuffer() {
      return this.byteBuffer;
   }

   public VertexFormat getVertexFormat() {
      return this.vertexFormat;
   }

   public int getVertexCount() {
      return this.vertexCount;
   }

   public int getDrawMode() {
      return this.drawMode;
   }

   public void putColor4(int var1) {
      for (int var5 = 0; var5 < 4; var5++) {
         this.putColor(var1, var5 + 1);
      }
   }

   public void putColorRGB_F4(float var1, float var2, float var3) {
      for (int var7 = 0; var7 < 4; var7++) {
         this.putColorRGB_F(var1, var2, var3, var7 + 1);
      }
   }

   public void putSprite(TextureAtlasSprite var1) {
      if (this.quadSprites != null) {
         int var5 = this.vertexCount / 4;
         this.quadSprites[var5 - 1] = var1;
      }
   }

   public void setSprite(TextureAtlasSprite var1) {
      if (this.quadSprites != null) {
         this.quadSprite = var1;
      }
   }

   public boolean isMultiTexture() {
      return this.quadSprites != null;
   }

   public void drawMultiTexture() {
      if (this.quadSprites != null) {
         int var4 = Config.getMinecraft().getTextureMapBlocks().getCountRegisteredSprites();
         if (this.drawnIcons.length <= var4) {
            this.drawnIcons = new boolean[var4 + 1];
         }

         Arrays.fill(this.drawnIcons, false);
         int var5 = 0;
         int var6 = -1;
         int var7 = this.vertexCount / 4;

         for (int var8 = 0; var8 < var7; var8++) {
            TextureAtlasSprite var9 = this.quadSprites[var8];
            if (var9 != null) {
               int var10 = var9.getIndexInMap();
               if (!this.drawnIcons[var10]) {
                  if (var9 == TextureUtils.iconGrassSideOverlay) {
                     if (var6 < 0) {
                        var6 = var8;
                     }
                  } else {
                     var8 = this.drawForIcon(var9, var8) - 1;
                     var5++;
                     if (this.blockLayer != EnumWorldBlockLayer.TRANSLUCENT) {
                        this.drawnIcons[var10] = true;
                     }
                  }
               }
            }
         }

         if (var6 >= 0) {
            this.drawForIcon(TextureUtils.iconGrassSideOverlay, var6);
            var5++;
         }

         if (var5 > 0) {
         }
      }
   }

   private int drawForIcon(TextureAtlasSprite var1, int var2) {
      GL11.glBindTexture(3553, var1.glSpriteTextureId);
      int var6 = -1;
      int var7 = -1;
      int var8 = this.vertexCount / 4;

      for (int var9 = var2; var9 < var8; var9++) {
         TextureAtlasSprite var10 = this.quadSprites[var9];
         if (var10 == var1) {
            if (var7 < 0) {
               var7 = var9;
            }
         } else if (var7 >= 0) {
            this.draw(var7, var9);
            if (this.blockLayer == EnumWorldBlockLayer.TRANSLUCENT) {
               return var9;
            }

            var7 = -1;
            if (var6 < 0) {
               var6 = var9;
            }
         }
      }

      if (var7 >= 0) {
         this.draw(var7, var8);
      }

      if (var6 < 0) {
         var6 = var8;
      }

      return var6;
   }

   private void draw(int var1, int var2) {
      int var6 = var2 - var1;
      if (var6 > 0) {
         int var7 = var1 * 4;
         int var8 = var6 * 4;
         GL11.glDrawArrays(this.drawMode, var7, var8);
      }
   }

   public void setBlockLayer(EnumWorldBlockLayer var1) {
      this.blockLayer = var1;
      if (var1 == null) {
         if (this.quadSprites != null) {
            this.quadSpritesPrev = this.quadSprites;
         }

         this.quadSprites = null;
         this.quadSprite = null;
      }
   }

   private int getBufferQuadSize() {
      return this.rawIntBuffer.capacity() * 4 / (this.vertexFormat.getIntegerSize() * 4);
   }

   public void H() {
      this.growBuffer(this.vertexFormat.getIntegerSize());
   }

   public boolean H_boolean() {
      return this.noColor;
   }

   public class State {
      private final int[] stateRawBuffer;
      private final VertexFormat stateVertexFormat;
      private TextureAtlasSprite[] stateQuadSprites;

      public State(int[] var2, VertexFormat var3, TextureAtlasSprite[] var4) {
         this.stateRawBuffer = var2;
         this.stateVertexFormat = var3;
         this.stateQuadSprites = var4;
      }

      public State(int[] var2, VertexFormat var3) {
         this.stateRawBuffer = var2;
         this.stateVertexFormat = var3;
      }

      public int[] getRawBuffer() {
         return this.stateRawBuffer;
      }

      public int getVertexCount() {
         return this.stateRawBuffer.length / this.stateVertexFormat.getIntegerSize();
      }

      public VertexFormat getVertexFormat() {
         return this.stateVertexFormat;
      }
   }
}
