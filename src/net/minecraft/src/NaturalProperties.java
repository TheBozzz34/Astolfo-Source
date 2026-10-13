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
   private static final long a = 134718308566848L;

   public NaturalProperties(String var1) {
      if (var1.equals("4")) {
         this.rotation = 4;
      } else if (var1.equals("2")) {
         this.rotation = 2;
      } else if (var1.equals("F")) {
         this.flip = true;
      } else if (var1.equals("4F")) {
         this.rotation = 4;
         this.flip = true;
      } else if (var1.equals("2F")) {
         this.rotation = 2;
         this.flip = true;
      } else {
         Config.warn("NaturalTextures: Unknown type: " + var1);
      }
   }

   public boolean isValid() {
      return this.rotation != 2 && this.rotation != 4 ? this.flip : true;
   }

   public synchronized BakedQuad getQuad(BakedQuad var1, int var2, boolean var3) {
      int var7 = var2;
      if (var3) {
         var7 = var2 | 4;
      }

      if (var7 > 0 && var7 < this.quadMaps.length) {
         Map var8 = this.quadMaps[var7];
         if (var8 == null) {
            var8 = new IdentityHashMap(1);
            this.quadMaps[var7] = var8;
         }

         BakedQuad var9 = (BakedQuad)var8.get(var1);
         if (var9 == null) {
            var9 = this.makeQuad(var1, var2, var3);
            var8.put(var1, var9);
         }

         return var9;
      } else {
         return var1;
      }
   }

   private BakedQuad makeQuad(BakedQuad var1, int var2, boolean var3) {
      int[] var7 = var1.getVertexData();
      int var8 = var1.getTintIndex();
      EnumFacing var9 = var1.getFace();
      TextureAtlasSprite var10 = var1.getSprite();
      if (!this.isFullSprite(var1)) {
         return var1;
      }

      var7 = this.transformVertexData(var7, var2, var3);
      return new BakedQuad(var7, var8, var9, var10);
   }

   private int[] transformVertexData(int[] var1, int var2, boolean var3) {
      int[] var7 = (int[])var1.clone();
      int var8 = 4 - var2;
      if (var3) {
         var8 += 3;
      }

      var8 %= 4;
      int var9 = var7.length / 4;

      for (int var10 = 0; var10 < 4; var10++) {
         int var11 = var10 * var9;
         int var12 = var8 * var9;
         var7[var12 + 4] = var1[var11 + 4];
         var7[var12 + 4 + 1] = var1[var11 + 4 + 1];
         if (var3) {
            if (--var8 < 0) {
               var8 = 3;
            }
         } else if (++var8 > 3) {
            var8 = 0;
         }
      }

      return var7;
   }

   private boolean isFullSprite(BakedQuad var1) {
      TextureAtlasSprite var5 = var1.getSprite();
      float var6 = var5.getMinU();
      float var7 = var5.getMaxU();
      float var8 = var7 - var6;
      float var9 = var8 / 256.0F;
      float var10 = var5.getMinV();
      float var11 = var5.getMaxV();
      float var12 = var11 - var10;
      float var13 = var12 / 256.0F;
      int[] var14 = var1.getVertexData();
      int var15 = var14.length / 4;

      for (int var16 = 0; var16 < 4; var16++) {
         int var17 = var16 * var15;
         float var18 = Float.intBitsToFloat(var14[var17 + 4]);
         float var19 = Float.intBitsToFloat(var14[var17 + 4 + 1]);
         if (!this.equalsDelta(var18, var6, var9) && !this.equalsDelta(var18, var7, var9)) {
            return false;
         }

         if (!this.equalsDelta(var19, var10, var13) && !this.equalsDelta(var19, var11, var13)) {
            return false;
         }
      }

      return true;
   }

   private boolean equalsDelta(float var1, float var2, float var3) {
      float var7 = MathHelper.abs(var1 - var2);
      return var7 < var3;
   }
}
