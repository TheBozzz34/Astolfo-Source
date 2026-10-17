package net.minecraft.client.renderer.block.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.IVertexProducer;

public class BakedQuad implements IVertexProducer {
   public int[] vertexData;
   public final int tintIndex;
   public final EnumFacing face;
   private TextureAtlasSprite sprite = null;
   private int[] vertexDataSingle = null;

   public BakedQuad(int[] ints, int i, EnumFacing enumFacing, TextureAtlasSprite textureAtlasSprite) {
      this.vertexData = ints;
      this.tintIndex = i;
      this.face = enumFacing;
      this.sprite = textureAtlasSprite;
      this.fixVertexData();
   }

   public TextureAtlasSprite getSprite() {
      if (this.sprite == null) {
         this.sprite = getSpriteByUv(this.getVertexData());
      }

      return this.sprite;
   }

   @Override
   public String toString() {
      return "vertex: " + this.vertexData.length / 7 + ", tint: " + this.tintIndex + ", facing: " + this.face + ", sprite: " + this.sprite;
   }

   public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn) {
      this.vertexData = vertexDataIn;
      this.tintIndex = tintIndexIn;
      this.face = faceIn;
      this.fixVertexData();
   }

   public int[] getVertexData() {
      this.fixVertexData();
      return this.vertexData;
   }

   public boolean hasTintIndex() {
      return this.tintIndex != -1;
   }

   public int getTintIndex() {
      return this.tintIndex;
   }

   public EnumFacing getFace() {
      return this.face;
   }

   public int[] getVertexDataSingle() {
      if (this.vertexDataSingle == null) {
         this.vertexDataSingle = makeVertexDataSingle(this.getVertexData(), this.getSprite());
      }

      return this.vertexDataSingle;
   }

   private static int[] makeVertexDataSingle(int[] ints, TextureAtlasSprite textureAtlasSprite) {
      int[] ints2 = (int[])ints.clone();
      int i = textureAtlasSprite.sheetWidth / textureAtlasSprite.getIconWidth();
      int j = textureAtlasSprite.sheetHeight / textureAtlasSprite.getIconHeight();
      int k = ints2.length / 4;

      for (int l = 0; l < 4; l++) {
         int m = l * k;
         float f = Float.intBitsToFloat(ints2[m + 4]);
         float g = Float.intBitsToFloat(ints2[m + 4 + 1]);
         float h = textureAtlasSprite.toSingleU(f);
         float f2 = textureAtlasSprite.toSingleV(g);
         ints2[m + 4] = Float.floatToRawIntBits(h);
         ints2[m + 4 + 1] = Float.floatToRawIntBits(f2);
      }

      return ints2;
   }

   @Override
   public void pipe(IVertexConsumer iVertexConsumer) {
      Reflector.callVoid(Reflector.LightUtil_putBakedQuad, iVertexConsumer, this);
   }

   private static TextureAtlasSprite getSpriteByUv(int[] ints) {
      float f = 1.0F;
      float g = 1.0F;
      float h = 0.0F;
      float f2 = 0.0F;
      int i = ints.length / 4;

      for (int j = 0; j < 4; j++) {
         int k = j * i;
         float f3 = Float.intBitsToFloat(ints[k + 4]);
         float f4 = Float.intBitsToFloat(ints[k + 4 + 1]);
         f = Math.min(f, f3);
         g = Math.min(g, f4);
         h = Math.max(h, f3);
         f2 = Math.max(f2, f4);
      }

      float f5 = (f + h) / 2.0F;
      float f6 = (g + f2) / 2.0F;
      return Minecraft.getMinecraft().getTextureMapBlocks().getIconByUV(f5, f6);
   }

   private void fixVertexData() {
      if (Config.isShaders()) {
         if (this.vertexData.length == 28) {
            this.vertexData = expandVertexData(this.vertexData);
         }
      } else if (this.vertexData.length == 56) {
         this.vertexData = compactVertexData(this.vertexData);
      }
   }

   private static int[] expandVertexData(int[] ints) {
      int i = ints.length / 4;
      int j = i * 2;
      int[] ints2 = new int[j * 4];

      for (int k = 0; k < 4; k++) {
         System.arraycopy(ints, k * i, ints2, k * j, i);
      }

      return ints2;
   }

   private static int[] compactVertexData(int[] ints) {
      int i = ints.length / 4;
      int j = i / 2;
      int[] ints2 = new int[j * 4];

      for (int k = 0; k < 4; k++) {
         System.arraycopy(ints, k * i, ints2, k * j, j);
      }

      return ints2;
   }
}
