package net.minecraft.src;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class NaturalProperties {
   public int rotation = 1;
   public boolean flip = false;
   private Map[] quadMaps = new Map[8];

   public NaturalProperties(String typeIn) {
      if (typeIn.equals("4")) {
         this.rotation = 4;
      } else if (typeIn.equals("2")) {
         this.rotation = 2;
      } else if (typeIn.equals("F")) {
         this.flip = true;
      } else if (typeIn.equals("4F")) {
         this.rotation = 4;
         this.flip = true;
      } else if (typeIn.equals("2F")) {
         this.rotation = 2;
         this.flip = true;
      } else {
         Config.warn("NaturalTextures: Unknown type: " + typeIn);
      }
   }

   public boolean isValid() {
      return this.rotation != 2 && this.rotation != 4 ? this.flip : true;
   }

   public synchronized BakedQuad getQuad(BakedQuad quadIn, int rotate, boolean flipU) {
      int i = rotate;
      if (flipU) {
         i = rotate | 4;
      }

      if (i > 0 && i < this.quadMaps.length) {
         Map map = this.quadMaps[i];
         if (map == null) {
            map = new IdentityHashMap(1);
            this.quadMaps[i] = map;
         }

         BakedQuad bakedQuad = (BakedQuad)map.get(quadIn);
         if (bakedQuad == null) {
            bakedQuad = this.makeQuad(quadIn, rotate, flipU);
            map.put(quadIn, bakedQuad);
         }

         return bakedQuad;
      } else {
         return quadIn;
      }
   }

   private BakedQuad makeQuad(BakedQuad quad, int rotate, boolean flipU) {
      int[] ints = quad.getVertexData();
      int i = quad.getTintIndex();
      EnumFacing enumFacing = quad.getFace();
      TextureAtlasSprite textureAtlasSprite = quad.getSprite();
      if (!this.isFullSprite(quad)) {
         return quad;
      }

      ints = this.transformVertexData(ints, rotate, flipU);
      return new BakedQuad(ints, i, enumFacing, textureAtlasSprite);
   }

   private int[] transformVertexData(int[] vertexData, int rotate, boolean flipU) {
      int[] ints = (int[])vertexData.clone();
      int i = 4 - rotate;
      if (flipU) {
         i += 3;
      }

      i %= 4;
      int j = ints.length / 4;

      for (int k = 0; k < 4; k++) {
         int l = k * j;
         int m = i * j;
         ints[m + 4] = vertexData[l + 4];
         ints[m + 4 + 1] = vertexData[l + 4 + 1];
         if (flipU) {
            if (--i < 0) {
               i = 3;
            }
         } else if (++i > 3) {
            i = 0;
         }
      }

      return ints;
   }

   private boolean isFullSprite(BakedQuad quad) {
      TextureAtlasSprite textureAtlasSprite = quad.getSprite();
      float f = textureAtlasSprite.getMinU();
      float g = textureAtlasSprite.getMaxU();
      float h = g - f;
      float f2 = h / 256.0F;
      float f3 = textureAtlasSprite.getMinV();
      float f4 = textureAtlasSprite.getMaxV();
      float f5 = f4 - f3;
      float f6 = f5 / 256.0F;
      int[] ints = quad.getVertexData();
      int i = ints.length / 4;

      for (int j = 0; j < 4; j++) {
         int k = j * i;
         float f7 = Float.intBitsToFloat(ints[k + 4]);
         float f8 = Float.intBitsToFloat(ints[k + 4 + 1]);
         if (!this.equalsDelta(f7, f, f2) && !this.equalsDelta(f7, g, f2)) {
            return false;
         }

         if (!this.equalsDelta(f8, f3, f6) && !this.equalsDelta(f8, f4, f6)) {
            return false;
         }
      }

      return true;
   }

   private boolean equalsDelta(float x1, float x2, float deltaMax) {
      float f = MathHelper.abs(x1 - x2);
      return f < deltaMax;
   }
}
