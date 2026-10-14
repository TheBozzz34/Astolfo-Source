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

   public Mipmaps(String iconName, int width, int height, int[] data, boolean direct) {
      this.iconName = iconName;
      this.width = width;
      this.height = height;
      this.data = data;
      this.direct = direct;
      this.mipmapDimensions = makeMipmapDimensions(width, height, iconName);
      this.mipmapDatas = generateMipMapData(data, width, height, this.mipmapDimensions);
      if (direct) {
         this.mipmapBuffers = makeMipmapBuffers(this.mipmapDimensions, this.mipmapDatas);
      }
   }

   public static Dimension[] makeMipmapDimensions(int width, int height, String iconName) {
      int i = TextureUtils.ceilPowerOfTwo(width);
      int j = TextureUtils.ceilPowerOfTwo(height);
      if (i == width && j == height) {
         ArrayList arrayList = new ArrayList();
         int k = i;
         int l = j;

         while (true) {
            k /= 2;
            l /= 2;
            if (k <= 0 && l <= 0) {
               return (java.awt.Dimension[])(arrayList.toArray(new Dimension[arrayList.size()]));
            }

            if (k <= 0) {
               k = 1;
            }

            if (l <= 0) {
               l = 1;
            }

            int m = k * l * 4;
            Dimension dimension = new Dimension(k, l);
            arrayList.add(dimension);
         }
      } else {
         Config.warn("Mipmaps not possible (power of 2 dimensions needed), texture: " + iconName + ", dim: " + width + "x" + height);
         return new Dimension[0];
      }
   }

   public static int[][] generateMipMapData(int[] data, int width, int height, Dimension[] mipmapDimensions) {
      int[] ints = data;
      int i = width;
      boolean bl = true;
      int[][] intss = new int[mipmapDimensions.length][];

      for (int j = 0; j < mipmapDimensions.length; j++) {
         Dimension dimension = mipmapDimensions[j];
         int k = dimension.width;
         int l = dimension.height;
         int[] ints2 = new int[k * l];
         intss[j] = ints2;
         int m = j + 1;
         if (bl) {
            for (int n = 0; n < k; n++) {
               for (int i2 = 0; i2 < l; i2++) {
                  int i3 = ints[n * 2 + 0 + (i2 * 2 + 0) * i];
                  int i4 = ints[n * 2 + 1 + (i2 * 2 + 0) * i];
                  int i5 = ints[n * 2 + 1 + (i2 * 2 + 1) * i];
                  int i6 = ints[n * 2 + 0 + (i2 * 2 + 1) * i];
                  int i7 = alphaBlend(i3, i4, i5, i6);
                  ints2[n + i2 * k] = i7;
               }
            }
         }

         ints = ints2;
         i = k;
         if (k <= 1 || l <= 1) {
            bl = false;
         }
      }

      return intss;
   }

   public static int alphaBlend(int c1, int c2, int c3, int c4) {
      int cx1 = alphaBlend(c1, c2);
      int cx2 = alphaBlend(c3, c4);
      return alphaBlend(cx1, cx2);
   }

   private static int alphaBlend(int c1, int c2) {
      int i = (c1 & 0xFF000000) >> 24 & 0xFF;
      int j = (c2 & 0xFF000000) >> 24 & 0xFF;
      int k = (i + j) / 2;
      if (i == 0 && j == 0) {
         i = 1;
         j = 1;
      } else {
         if (i == 0) {
            c1 = c2;
            k /= 2;
         }

         if (j == 0) {
            c2 = c1;
            k /= 2;
         }
      }

      int l = (c1 >> 16 & 0xFF) * i;
      int m = (c1 >> 8 & 0xFF) * i;
      int n = (c1 & 0xFF) * i;
      int i2 = (c2 >> 16 & 0xFF) * j;
      int i3 = (c2 >> 8 & 0xFF) * j;
      int i4 = (c2 & 0xFF) * j;
      int i5 = (l + i2) / (i + j);
      int i6 = (m + i3) / (i + j);
      int i7 = (n + i4) / (i + j);
      return k << 24 | i5 << 16 | i6 << 8 | i7;
   }

   public static IntBuffer[] makeMipmapBuffers(Dimension[] mipmapDimensions, int[][] mipmapDatas) {
      if (mipmapDimensions == null) {
         return null;
      }

      IntBuffer[] intBuffers = new IntBuffer[mipmapDimensions.length];

      for (int i = 0; i < mipmapDimensions.length; i++) {
         Dimension dimension = mipmapDimensions[i];
         int j = dimension.width * dimension.height;
         IntBuffer intBuffer = GLAllocation.createDirectIntBuffer(j);
         int[] ints = mipmapDatas[i];
         ((Buffer)intBuffer).clear();
         intBuffer.put(ints);
         ((Buffer)intBuffer).clear();
         intBuffers[i] = intBuffer;
      }

      return intBuffers;
   }

   public static void allocateMipmapTextures(int width, int height, String name) {
      Dimension[] dimensions = makeMipmapDimensions(width, height, name);

      for (int i = 0; i < dimensions.length; i++) {
         Dimension dimension = dimensions[i];
         int j = dimension.width;
         int k = dimension.height;
         int l = i + 1;
         GL11.glTexImage2D(3553, l, 6408, j, k, 0, 32993, 33639, (IntBuffer)null);
      }
   }
}
