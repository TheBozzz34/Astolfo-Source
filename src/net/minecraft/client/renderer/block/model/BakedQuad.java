package net.minecraft.client.renderer.block.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.IVertexProducer;

public class BakedQuad implements IVertexProducer {
   public int[] u;
   public final int tintIndex;
   public final EnumFacing face;
   private TextureAtlasSprite sprite = null;
   private int[] vertexDataSingle = null;
   private static final long a = 2360413664131L;

   public BakedQuad(int[] var1, int var2, EnumFacing var3, TextureAtlasSprite var4) {
      this.u = var1;
      this.tintIndex = var2;
      this.face = var3;
      this.sprite = var4;
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
      return "vertex: " + this.u.length / 7 + ", tint: " + this.tintIndex + ", facing: " + this.face + ", sprite: " + this.sprite;
   }

   public BakedQuad(int[] var1, int var2, EnumFacing var3) {
      this.u = var1;
      this.tintIndex = var2;
      this.face = var3;
      this.fixVertexData();
   }

   public int[] getVertexData() {
      this.fixVertexData();
      return this.u;
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

   private static int[] makeVertexDataSingle(int[] var0, TextureAtlasSprite var1) {
      int[] var5 = (int[])var0.clone();
      int var6 = var1.sheetWidth / var1.getIconWidth();
      int var7 = var1.sheetHeight / var1.getIconHeight();
      int var8 = var5.length / 4;

      for (int var9 = 0; var9 < 4; var9++) {
         int var10 = var9 * var8;
         float var11 = Float.intBitsToFloat(var5[var10 + 4]);
         float var12 = Float.intBitsToFloat(var5[var10 + 4 + 1]);
         float var13 = var1.toSingleU(var11);
         float var14 = var1.toSingleV(var12);
         var5[var10 + 4] = Float.floatToRawIntBits(var13);
         var5[var10 + 4 + 1] = Float.floatToRawIntBits(var14);
      }

      return var5;
   }

   @Override
   public void pipe(IVertexConsumer var1) {
      Reflector.callVoid(Reflector.LightUtil_putBakedQuad, var1, this);
   }

   private static TextureAtlasSprite getSpriteByUv(int[] var0) {
      float var4 = 1.0F;
      float var5 = 1.0F;
      float var6 = 0.0F;
      float var7 = 0.0F;
      int var8 = var0.length / 4;

      for (int var9 = 0; var9 < 4; var9++) {
         int var10 = var9 * var8;
         float var11 = Float.intBitsToFloat(var0[var10 + 4]);
         float var12 = Float.intBitsToFloat(var0[var10 + 4 + 1]);
         var4 = Math.min(var4, var11);
         var5 = Math.min(var5, var12);
         var6 = Math.max(var6, var11);
         var7 = Math.max(var7, var12);
      }

      float var13 = (var4 + var6) / 2.0F;
      float var14 = (var5 + var7) / 2.0F;
      return Minecraft.getMinecraft().getTextureMapBlocks().getIconByUV(var13, var14);
   }

   private void fixVertexData() {
      if (Config.isShaders()) {
         if (this.u.length == 28) {
            this.u = expandVertexData(this.u);
         }
      } else if (this.u.length == 56) {
         this.u = compactVertexData(this.u);
      }
   }

   private static int[] expandVertexData(int[] var0) {
      int var4 = var0.length / 4;
      int var5 = var4 * 2;
      int[] var6 = new int[var5 * 4];

      for (int var7 = 0; var7 < 4; var7++) {
         System.arraycopy(var0, var7 * var4, var6, var7 * var5, var4);
      }

      return var6;
   }

   private static int[] compactVertexData(int[] var0) {
      int var4 = var0.length / 4;
      int var5 = var4 / 2;
      int[] var6 = new int[var5 * 4];

      for (int var7 = 0; var7 < 4; var7++) {
         System.arraycopy(var0, var7 * var4, var6, var7 * var5, var5);
      }

      return var6;
   }
}
