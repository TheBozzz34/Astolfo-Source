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

   public WorldRenderer(int bufferSizeIn) {
      if (Config.isShaders()) {
         bufferSizeIn *= 2;
      }

      this.byteBuffer = GLAllocation.createDirectByteBuffer(bufferSizeIn * 4);
      this.rawIntBuffer = this.byteBuffer.asIntBuffer();
      this.rawShortBuffer = this.byteBuffer.asShortBuffer();
      this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
      SVertexBuilder.initVertexBuilder(this);
   }

   private void growBuffer(int i) {
      if (Config.isShaders()) {
         i *= 2;
      }

      if (i > this.rawIntBuffer.remaining()) {
         int j = this.byteBuffer.capacity();
         int k = j % 2097152;
         int l = k + (((this.rawIntBuffer.position() + i) * 4 - k) / 2097152 + 1) * 2097152;
         LogManager.getLogger().warn("Needed to grow BufferBuilder buffer: Old size " + j + " bytes, new size " + l + " bytes.");
         int m = this.rawIntBuffer.position();
         ByteBuffer byteBuffer2 = GLAllocation.createDirectByteBuffer(l);
         ((Buffer)this.byteBuffer).position(0);
         byteBuffer2.put(this.byteBuffer);
         ((Buffer)byteBuffer2).rewind();
         this.byteBuffer = byteBuffer2;
         this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
         this.rawIntBuffer = this.byteBuffer.asIntBuffer();
         ((Buffer)this.rawIntBuffer).position(m);
         this.rawShortBuffer = this.byteBuffer.asShortBuffer();
         ((Buffer)this.rawShortBuffer).position(m << 1);
         if (this.quadSprites != null) {
            TextureAtlasSprite[] textureAtlasSprites = this.quadSprites;
            int n = this.getBufferQuadSize();
            this.quadSprites = new TextureAtlasSprite[n];
            System.arraycopy(textureAtlasSprites, 0, this.quadSprites, 0, Math.min(textureAtlasSprites.length, this.quadSprites.length));
            this.quadSpritesPrev = null;
         }
      }
   }

   public void sortVertexData(float f, float g, float h) {
      int i = this.vertexCount / 4;
      final float[] floats = new float[i];

      for (int j = 0; j < i; j++) {
         floats[j] = getDistanceSq(
            this.rawFloatBuffer,
            (float)(f + this.xOffset),
            (float)(g + this.yOffset),
            (float)(h + this.zOffset),
            this.vertexFormat.getIntegerSize(),
            j * this.vertexFormat.getNextOffset()
         );
      }

      Integer[] integers = new Integer[i];

      for (int k = 0; k < integers.length; k++) {
         integers[k] = k;
      }

      Arrays.sort(integers, new Comparator<Integer>() {
         public int compare(Integer integer, Integer integer2) {
            return Floats.compare(floats[integer2], floats[integer]);
         }
      });
      BitSet bitSet = new BitSet();
      int l = this.vertexFormat.getNextOffset();
      int[] ints = new int[l];

      for (int m = 0; (m = bitSet.nextClearBit(m)) < integers.length; m++) {
         int n = integers[m];
         if (n != m) {
            ((Buffer)this.rawIntBuffer).limit(n * l + l);
            ((Buffer)this.rawIntBuffer).position(n * l);
            this.rawIntBuffer.get(ints);
            int i2 = n;

            for (int i3 = integers[n]; i2 != m; i3 = integers[i3]) {
               ((Buffer)this.rawIntBuffer).limit(i3 * l + l);
               ((Buffer)this.rawIntBuffer).position(i3 * l);
               IntBuffer intBuffer = this.rawIntBuffer.slice();
               ((Buffer)this.rawIntBuffer).limit(i2 * l + l);
               ((Buffer)this.rawIntBuffer).position(i2 * l);
               this.rawIntBuffer.put(intBuffer);
               bitSet.set(i2);
               i2 = i3;
            }

            ((Buffer)this.rawIntBuffer).limit(m * l + l);
            ((Buffer)this.rawIntBuffer).position(m * l);
            this.rawIntBuffer.put(ints);
         }

         bitSet.set(m);
      }

      ((Buffer)this.rawIntBuffer).limit(this.rawIntBuffer.capacity());
      ((Buffer)this.rawIntBuffer).position(this.getBufferSize());
      if (this.quadSprites != null) {
         TextureAtlasSprite[] textureAtlasSprites = new TextureAtlasSprite[this.vertexCount / 4];
         int i4 = this.vertexFormat.getIntegerSize() / 4 * 4;

         for (int i5 = 0; i5 < integers.length; i5++) {
            int i6 = integers[i5];
            textureAtlasSprites[i5] = this.quadSprites[i6];
         }

         System.arraycopy(textureAtlasSprites, 0, this.quadSprites, 0, textureAtlasSprites.length);
      }
   }

   public WorldRenderer.State getVertexState() {
      ((Buffer)this.rawIntBuffer).rewind();
      int i = this.getBufferSize();
      ((Buffer)this.rawIntBuffer).limit(i);
      int[] ints = new int[i];
      this.rawIntBuffer.get(ints);
      ((Buffer)this.rawIntBuffer).limit(this.rawIntBuffer.capacity());
      ((Buffer)this.rawIntBuffer).position(i);
      TextureAtlasSprite[] textureAtlasSprites = null;
      if (this.quadSprites != null) {
         int j = this.vertexCount / 4;
         textureAtlasSprites = new TextureAtlasSprite[j];
         System.arraycopy(this.quadSprites, 0, textureAtlasSprites, 0, j);
      }

      return new WorldRenderer.State(ints, new VertexFormat(this.vertexFormat), textureAtlasSprites);
   }

   public int getBufferSize() {
      return this.vertexCount * this.vertexFormat.getIntegerSize();
   }

   private static float getDistanceSq(FloatBuffer floatBuffer, float f, float g, float h, int i, int j) {
      float f2 = floatBuffer.get(j + i * 0 + 0);
      float f3 = floatBuffer.get(j + i * 0 + 1);
      float f4 = floatBuffer.get(j + i * 0 + 2);
      float f5 = floatBuffer.get(j + i * 1 + 0);
      float f6 = floatBuffer.get(j + i * 1 + 1);
      float f7 = floatBuffer.get(j + i * 1 + 2);
      float f8 = floatBuffer.get(j + i * 2 + 0);
      float f9 = floatBuffer.get(j + i * 2 + 1);
      float f10 = floatBuffer.get(j + i * 2 + 2);
      float f11 = floatBuffer.get(j + i * 3 + 0);
      float f12 = floatBuffer.get(j + i * 3 + 1);
      float f13 = floatBuffer.get(j + i * 3 + 2);
      float f14 = (f2 + f5 + f8 + f11) * 0.25F - f;
      float f15 = (f3 + f6 + f9 + f12) * 0.25F - g;
      float f16 = (f4 + f7 + f10 + f13) * 0.25F - h;
      return f14 * f14 + f15 * f15 + f16 * f16;
   }

   public void setVertexState(WorldRenderer.State state) {
      ((Buffer)this.rawIntBuffer).clear();
      this.growBuffer(state.getRawBuffer().length);
      this.rawIntBuffer.put(state.getRawBuffer());
      this.vertexCount = state.getVertexCount();
      this.vertexFormat = new VertexFormat(state.getVertexFormat());
      if (state.stateQuadSprites != null) {
         if (this.quadSprites == null) {
            this.quadSprites = this.quadSpritesPrev;
         }

         if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize()) {
            this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
         }

         TextureAtlasSprite[] textureAtlasSprites = state.stateQuadSprites;
         System.arraycopy(textureAtlasSprites, 0, this.quadSprites, 0, textureAtlasSprites.length);
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

   public void begin(int glMode, VertexFormat formatIn) {
      if (this.isDrawing) {
         throw new IllegalStateException("Already building!");
      }

      this.isDrawing = true;
      this.reset();
      this.drawMode = glMode;
      this.vertexFormat = formatIn;
      this.vertexFormatElement = formatIn.getElement(this.vertexFormatIndex);
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
   public WorldRenderer tex(double u, double v) {
      if (this.quadSprite != null && this.quadSprites != null) {
         u = this.quadSprite.toSingleU((float)u);
         v = this.quadSprite.toSingleV((float)v);
         this.quadSprites[this.vertexCount / 4] = this.quadSprite;
      }

      int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(i, (float)u);
            this.byteBuffer.putFloat(i + 4, (float)v);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(i, (int)u);
            this.byteBuffer.putInt(i + 4, (int)v);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(i, (short)v);
            this.byteBuffer.putShort(i + 2, (short)u);
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(i, (byte)v);
            this.byteBuffer.put(i + 1, (byte)u);
      }

      this.nextVertexFormatIndex();
      return this;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public WorldRenderer lightmap(int i, int j) {
      int k = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(k, i);
            this.byteBuffer.putFloat(k + 4, j);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(k, i);
            this.byteBuffer.putInt(k + 4, j);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(k, (short)j);
            this.byteBuffer.putShort(k + 2, (short)i);
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(k, (byte)j);
            this.byteBuffer.put(k + 1, (byte)i);
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void putBrightness4(int i, int j, int k, int l) {
      int m = (this.vertexCount - 4) * this.vertexFormat.getIntegerSize() + this.vertexFormat.getUvOffsetById(1) / 4;
      int n = this.vertexFormat.getNextOffset() >> 2;
      this.rawIntBuffer.put(m, i);
      this.rawIntBuffer.put(m + n, j);
      this.rawIntBuffer.put(m + n * 2, k);
      this.rawIntBuffer.put(m + n * 3, l);
   }

   public void putPosition(double x, double y, double z) {
      int i = this.vertexFormat.getIntegerSize();
      int j = (this.vertexCount - 4) * i;

      for (int k = 0; k < 4; k++) {
         int l = j + k * i;
         int m = l + 1;
         int n = m + 1;
         this.rawIntBuffer.put(l, Float.floatToRawIntBits((float)(x + this.xOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(l))));
         this.rawIntBuffer.put(m, Float.floatToRawIntBits((float)(y + this.yOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(m))));
         this.rawIntBuffer.put(n, Float.floatToRawIntBits((float)(z + this.zOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(n))));
      }
   }

   public int getColorIndex(int i) {
      return ((this.vertexCount - i) * this.vertexFormat.getNextOffset() + this.vertexFormat.getColorOffset()) / 4;
   }

   public void putColorMultiplier(float red, float green, float blue, int i) {
      int j = this.getColorIndex(i);
      int k = -1;
      if (!this.noColor) {
         k = this.rawIntBuffer.get(j);
         if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            int l = (int)((k & 0xFF) * red);
            int m = (int)((k >> 8 & 0xFF) * green);
            int n = (int)((k >> 16 & 0xFF) * blue);
            k &= -16777216;
            k = k | n << 16 | m << 8 | l;
         } else {
            int i2 = (int)((k >> 24 & 0xFF) * red);
            int i3 = (int)((k >> 16 & 0xFF) * green);
            int i4 = (int)((k >> 8 & 0xFF) * blue);
            k &= 255;
            k = k | i2 << 24 | i3 << 16 | i4 << 8;
         }
      }

      if (ModuleManager.s.isEnabled() && Xray.isTranslucent()) {
         this.rawIntBuffer.put(j, MathHelper.floor_float(Xray.opacity.getValue().intValue() * 2.3F) + 25 << 24 | k & 16777215);
      } else {
         this.rawIntBuffer.put(j, k);
      }
   }

   private void putColor(int argb, int i) {
      int j = this.getColorIndex(i);
      int k = argb >> 16 & 0xFF;
      int l = argb >> 8 & 0xFF;
      int m = argb & 0xFF;
      int n = argb >> 24 & 0xFF;
      this.putColorRGBA(j, k, l, m, n);
   }

   public void putColorRGB_F(float red, float green, float blue, int i) {
      int j = this.getColorIndex(i);
      int k = MathHelper.clamp_int((int)(red * 255.0F), 0, 255);
      int l = MathHelper.clamp_int((int)(green * 255.0F), 0, 255);
      int m = MathHelper.clamp_int((int)(blue * 255.0F), 0, 255);
      this.putColorRGBA(j, k, l, m, 255);
   }

   public void putColorRGBA(int index, int red, int i, int j, int k) {
      if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
         this.rawIntBuffer.put(index, k << 24 | j << 16 | i << 8 | red);
      } else {
         this.rawIntBuffer.put(index, red << 24 | i << 16 | j << 8 | k);
      }
   }

   public void noColor() {
      this.noColor = true;
   }

   public WorldRenderer color(float red, float green, float blue, float alpha) {
      return this.color((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public WorldRenderer color(int red, int green, int blue, int alpha) {
      if (this.noColor) {
         return this;
      }

      int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(i, red / 255.0F);
            this.byteBuffer.putFloat(i + 4, green / 255.0F);
            this.byteBuffer.putFloat(i + 8, blue / 255.0F);
            this.byteBuffer.putFloat(i + 12, alpha / 255.0F);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putFloat(i, red);
            this.byteBuffer.putFloat(i + 4, green);
            this.byteBuffer.putFloat(i + 8, blue);
            this.byteBuffer.putFloat(i + 12, alpha);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(i, (short)red);
            this.byteBuffer.putShort(i + 2, (short)green);
            this.byteBuffer.putShort(i + 4, (short)blue);
            this.byteBuffer.putShort(i + 6, (short)alpha);
            break;
         case UBYTE:
         case BYTE:
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
               this.byteBuffer.put(i, (byte)red);
               this.byteBuffer.put(i + 1, (byte)green);
               this.byteBuffer.put(i + 2, (byte)blue);
               this.byteBuffer.put(i + 3, (byte)alpha);
            } else {
               this.byteBuffer.put(i, (byte)alpha);
               this.byteBuffer.put(i + 1, (byte)blue);
               this.byteBuffer.put(i + 2, (byte)green);
               this.byteBuffer.put(i + 3, (byte)red);
            }
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void addVertexData(int[] vertexData) {
      if (Config.isShaders()) {
         SVertexBuilder.beginAddVertexData(this, vertexData);
      }

      this.growBuffer(vertexData.length);
      ((Buffer)this.rawIntBuffer).position(this.getBufferSize());
      this.rawIntBuffer.put(vertexData);
      this.vertexCount = this.vertexCount + vertexData.length / this.vertexFormat.getIntegerSize();
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

   public void l(int i) {
      int j = i >> 16 & 0xFF;
      int k = i >> 8 & 0xFF;
      int m = i & 0xFF;
      this.putPosition(j, k, m);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public WorldRenderer pos(double x, double y, double z) {
      if (Config.isShaders()) {
         SVertexBuilder.beginAddVertex(this);
      }

      int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(i, (float)(x + this.xOffset));
            this.byteBuffer.putFloat(i + 4, (float)(y + this.yOffset));
            this.byteBuffer.putFloat(i + 8, (float)(z + this.zOffset));
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(i, Float.floatToRawIntBits((float)(x + this.xOffset)));
            this.byteBuffer.putInt(i + 4, Float.floatToRawIntBits((float)(y + this.yOffset)));
            this.byteBuffer.putInt(i + 8, Float.floatToRawIntBits((float)(z + this.zOffset)));
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(i, (short)(x + this.xOffset));
            this.byteBuffer.putShort(i + 2, (short)(y + this.yOffset));
            this.byteBuffer.putShort(i + 4, (short)(z + this.zOffset));
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(i, (byte)(x + this.xOffset));
            this.byteBuffer.put(i + 1, (byte)(y + this.yOffset));
            this.byteBuffer.put(i + 2, (byte)(z + this.zOffset));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void putNormal(float x, float y, float z) {
      int i = (byte)(x * 127.0F) & 255;
      int j = (byte)(y * 127.0F) & 255;
      int k = (byte)(z * 127.0F) & 255;
      int l = i | j << 8 | k << 16;
      int m = this.vertexFormat.getNextOffset() >> 2;
      int n = (this.vertexCount - 4) * m + this.vertexFormat.getNormalOffset() / 4;
      this.rawIntBuffer.put(n, l);
      this.rawIntBuffer.put(n + m, l);
      this.rawIntBuffer.put(n + m * 2, l);
      this.rawIntBuffer.put(n + m * 3, l);
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
   public WorldRenderer normal(float f, float g, float h) {
      int i = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.getOffset(this.vertexFormatIndex);
      switch (this.vertexFormatElement.getType()) {
         case FLOAT:
            this.byteBuffer.putFloat(i, f);
            this.byteBuffer.putFloat(i + 4, g);
            this.byteBuffer.putFloat(i + 8, h);
            break;
         case UINT:
         case INT:
            this.byteBuffer.putInt(i, (int)f);
            this.byteBuffer.putInt(i + 4, (int)g);
            this.byteBuffer.putInt(i + 8, (int)h);
            break;
         case USHORT:
         case SHORT:
            this.byteBuffer.putShort(i, (short)((int)(f * 32767.0F) & 65535));
            this.byteBuffer.putShort(i + 2, (short)((int)(g * 32767.0F) & 65535));
            this.byteBuffer.putShort(i + 4, (short)((int)(h * 32767.0F) & 65535));
            break;
         case UBYTE:
         case BYTE:
            this.byteBuffer.put(i, (byte)((int)(f * 127.0F) & 0xFF));
            this.byteBuffer.put(i + 1, (byte)((int)(g * 127.0F) & 0xFF));
            this.byteBuffer.put(i + 2, (byte)((int)(h * 127.0F) & 0xFF));
      }

      this.nextVertexFormatIndex();
      return this;
   }

   public void setTranslation(double x, double y, double z) {
      this.xOffset = x;
      this.yOffset = y;
      this.zOffset = z;
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

   public void putColor4(int argb) {
      for (int i = 0; i < 4; i++) {
         this.putColor(argb, i + 1);
      }
   }

   public void putColorRGB_F4(float red, float green, float blue) {
      for (int i = 0; i < 4; i++) {
         this.putColorRGB_F(red, green, blue, i + 1);
      }
   }

   public void putSprite(TextureAtlasSprite textureAtlasSprite) {
      if (this.quadSprites != null) {
         int i = this.vertexCount / 4;
         this.quadSprites[i - 1] = textureAtlasSprite;
      }
   }

   public void setSprite(TextureAtlasSprite textureAtlasSprite) {
      if (this.quadSprites != null) {
         this.quadSprite = textureAtlasSprite;
      }
   }

   public boolean isMultiTexture() {
      return this.quadSprites != null;
   }

   public void drawMultiTexture() {
      if (this.quadSprites != null) {
         int i = Config.getMinecraft().getTextureMapBlocks().getCountRegisteredSprites();
         if (this.drawnIcons.length <= i) {
            this.drawnIcons = new boolean[i + 1];
         }

         Arrays.fill(this.drawnIcons, false);
         int j = 0;
         int k = -1;
         int l = this.vertexCount / 4;

         for (int m = 0; m < l; m++) {
            TextureAtlasSprite textureAtlasSprite = this.quadSprites[m];
            if (textureAtlasSprite != null) {
               int n = textureAtlasSprite.getIndexInMap();
               if (!this.drawnIcons[n]) {
                  if (textureAtlasSprite == TextureUtils.iconGrassSideOverlay) {
                     if (k < 0) {
                        k = m;
                     }
                  } else {
                     m = this.drawForIcon(textureAtlasSprite, m) - 1;
                     j++;
                     if (this.blockLayer != EnumWorldBlockLayer.TRANSLUCENT) {
                        this.drawnIcons[n] = true;
                     }
                  }
               }
            }
         }

         if (k >= 0) {
            this.drawForIcon(TextureUtils.iconGrassSideOverlay, k);
            j++;
         }

         if (j > 0) {
         }
      }
   }

   private int drawForIcon(TextureAtlasSprite textureAtlasSprite, int i) {
      GL11.glBindTexture(3553, textureAtlasSprite.glSpriteTextureId);
      int j = -1;
      int k = -1;
      int l = this.vertexCount / 4;

      for (int m = i; m < l; m++) {
         TextureAtlasSprite textureAtlasSprite2 = this.quadSprites[m];
         if (textureAtlasSprite2 == textureAtlasSprite) {
            if (k < 0) {
               k = m;
            }
         } else if (k >= 0) {
            this.draw(k, m);
            if (this.blockLayer == EnumWorldBlockLayer.TRANSLUCENT) {
               return m;
            }

            k = -1;
            if (j < 0) {
               j = m;
            }
         }
      }

      if (k >= 0) {
         this.draw(k, l);
      }

      if (j < 0) {
         j = l;
      }

      return j;
   }

   private void draw(int i, int j) {
      int k = j - i;
      if (k > 0) {
         int l = i * 4;
         int m = k * 4;
         GL11.glDrawArrays(this.drawMode, l, m);
      }
   }

   public void setBlockLayer(EnumWorldBlockLayer enumWorldBlockLayer) {
      this.blockLayer = enumWorldBlockLayer;
      if (enumWorldBlockLayer == null) {
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

      public State(int[] ints, VertexFormat vertexFormat2, TextureAtlasSprite[] textureAtlasSprites) {
         this.stateRawBuffer = ints;
         this.stateVertexFormat = vertexFormat2;
         this.stateQuadSprites = textureAtlasSprites;
      }

      public State(int[] buffer, VertexFormat formatIn) {
         this.stateRawBuffer = buffer;
         this.stateVertexFormat = formatIn;
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
