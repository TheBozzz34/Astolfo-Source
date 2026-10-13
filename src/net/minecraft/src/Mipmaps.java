package net.minecraft.src;

import java.awt.Dimension;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.GL11;

public class Mipmaps {
   private final String iconName;
   private final int width;
   private final int height;
   private final int[] data;
   private final boolean direct;
   private int[][] mipmapDatas;
   private IntBuffer[] mipmapBuffers;
   private Dimension[] mipmapDimensions;
   private static final long a = 101500526968851L;

   public Mipmaps(String var1, int var2, int var3, int[] var4, boolean var5) {
      this.iconName = var1;
      this.width = var2;
      this.height = var3;
      this.data = var4;
      this.direct = var5;
      this.mipmapDimensions = makeMipmapDimensions(var2, var3, var1);
      this.mipmapDatas = generateMipMapData(var4, var2, var3, this.mipmapDimensions);
      if (var5) {
         this.mipmapBuffers = makeMipmapBuffers(this.mipmapDimensions, this.mipmapDatas);
      }
   }

   public static Dimension[] makeMipmapDimensions(int var0, int var1, String var2) {
      int var6 = TextureUtils.ceilPowerOfTwo(var0);
      int var7 = TextureUtils.ceilPowerOfTwo(var1);
      if (var6 == var0 && var7 == var1) {
         ArrayList var8 = new ArrayList();
         int var9 = var6;
         int var10 = var7;

         while (true) {
            var9 /= 2;
            var10 /= 2;
            if (var9 <= 0 && var10 <= 0) {
               return (java.awt.Dimension[])(var8.toArray(new Dimension[var8.size()]));
            }

            if (var9 <= 0) {
               var9 = 1;
            }

            if (var10 <= 0) {
               var10 = 1;
            }

            int var11 = var9 * var10 * 4;
            Dimension var12 = new Dimension(var9, var10);
            var8.add(var12);
         }
      } else {
         Config.warn("Mipmaps not possible (power of 2 dimensions needed), texture: " + var2 + ", dim: " + var0 + "x" + var1);
         return new Dimension[0];
      }
   }

   public static int[][] generateMipMapData(int[] var0, int var1, int var2, Dimension[] var3) {
      int[] var7 = var0;
      int var8 = var1;
      boolean var9 = true;
      int[][] var10 = new int[var3.length][];

      for (int var11 = 0; var11 < var3.length; var11++) {
         Dimension var12 = var3[var11];
         int var13 = var12.width;
         int var14 = var12.height;
         int[] var15 = new int[var13 * var14];
         var10[var11] = var15;
         int var16 = var11 + 1;
         if (var9) {
            for (int var17 = 0; var17 < var13; var17++) {
               for (int var18 = 0; var18 < var14; var18++) {
                  int var19 = var7[var17 * 2 + 0 + (var18 * 2 + 0) * var8];
                  int var20 = var7[var17 * 2 + 1 + (var18 * 2 + 0) * var8];
                  int var21 = var7[var17 * 2 + 1 + (var18 * 2 + 1) * var8];
                  int var22 = var7[var17 * 2 + 0 + (var18 * 2 + 1) * var8];
                  int var23 = alphaBlend(var19, var20, var21, var22);
                  var15[var17 + var18 * var13] = var23;
               }
            }
         }

         var7 = var15;
         var8 = var13;
         if (var13 <= 1 || var14 <= 1) {
            var9 = false;
         }
      }

      return var10;
   }

   public static int alphaBlend(int var0, int var1, int var2, int var3) {
      int var4 = alphaBlend(var0, var1);
      int var5 = alphaBlend(var2, var3);
      return alphaBlend(var4, var5);
   }

   private static int alphaBlend(int var0, int var1) {
      int var5 = (var0 & 0xFF000000) >> 24 & 0xFF;
      int var6 = (var1 & 0xFF000000) >> 24 & 0xFF;
      int var7 = (var5 + var6) / 2;
      if (var5 == 0 && var6 == 0) {
         var5 = 1;
         var6 = 1;
      } else {
         if (var5 == 0) {
            var0 = var1;
            var7 /= 2;
         }

         if (var6 == 0) {
            var1 = var0;
            var7 /= 2;
         }
      }

      int var8 = (var0 >> 16 & 0xFF) * var5;
      int var9 = (var0 >> 8 & 0xFF) * var5;
      int var10 = (var0 & 0xFF) * var5;
      int var11 = (var1 >> 16 & 0xFF) * var6;
      int var12 = (var1 >> 8 & 0xFF) * var6;
      int var13 = (var1 & 0xFF) * var6;
      int var14 = (var8 + var11) / (var5 + var6);
      int var15 = (var9 + var12) / (var5 + var6);
      int var16 = (var10 + var13) / (var5 + var6);
      return var7 << 24 | var14 << 16 | var15 << 8 | var16;
   }

   public static IntBuffer[] makeMipmapBuffers(Dimension[] var0, int[][] var1) {
      if (var0 == null) {
         return null;
      }

      IntBuffer[] var5 = new IntBuffer[var0.length];

      for (int var6 = 0; var6 < var0.length; var6++) {
         Dimension var7 = var0[var6];
         int var8 = var7.width * var7.height;
         IntBuffer var9 = GLAllocation.createDirectIntBuffer(var8);
         int[] var10 = var1[var6];
         ((Buffer)var9).clear();
         var9.put(var10);
         ((Buffer)var9).clear();
         var5[var6] = var9;
      }

      return var5;
   }

   public static void allocateMipmapTextures(int var0, int var1, String var2) {
      long var3 = a ^ 87871509747799L;
      Dimension[] var6 = makeMipmapDimensions(var0, var1, var2);

      for (int var7 = 0; var7 < var6.length; var7++) {
         Dimension var8 = var6[var7];
         int var9 = var8.width;
         long var10000 = 8155406558575633132L ^ var3;
         int var10 = var8.height;
         int var11 = var7 + 1;
         GL11.glTexImage2D(3553, var11, 6408, var9, var10, 0, 32993, 33639, (IntBuffer)null);
      }
   }
}
