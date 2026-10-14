package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.src.Config;
import net.minecraft.src.Mipmaps;
import net.minecraft.src.Reflector;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class TextureUtil {
   private static final Logger logger = LogManager.getLogger();
   private static final IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(4194304);
   public static final DynamicTexture missingTexture = new DynamicTexture(16, 16);
   public static final int[] missingTextureData = missingTexture.getTextureData();
   private static final int[] mipmapBuffer;

   public static int glGenTextures() {
      return GlStateManager.generateTexture();
   }

   public static void deleteTexture(int textureId) {
      GlStateManager.deleteTexture(textureId);
   }

   public static int uploadTextureImage(int i, BufferedImage bufferedImage) {
      return uploadTextureImageAllocate(i, bufferedImage, false, false);
   }

   public static void uploadTexture(int textureId, int[] ints, int i, int j) {
      bindTexture(textureId);
      uploadTextureSub(0, ints, i, j, 0, 0, false, false, false);
   }

   public static int[][] generateMipmapData(int i, int j, int[][] ints) {
      int[][] intss = new int[i + 1][];
      intss[0] = ints[0];
      if (i > 0) {
         boolean bl = false;

         for (int k = 0; k < ints.length; k++) {
            if (ints[0][k] >> 24 == 0) {
               bl = true;
               break;
            }
         }

         for (int l = 1; l <= i; l++) {
            if (ints[l] != null) {
               intss[l] = ints[l];
            } else {
               int[] ints2 = intss[l - 1];
               int[] ints3 = new int[ints2.length >> 2];
               int m = j >> l;
               int n = ints3.length / m;
               int i2 = m << 1;

               for (int i3 = 0; i3 < m; i3++) {
                  for (int i4 = 0; i4 < n; i4++) {
                     int i5 = 2 * (i3 + i4 * i2);
                     ints3[i3 + i4 * m] = blendColors(ints2[i5 + 0], ints2[i5 + 1], ints2[i5 + 0 + i2], ints2[i5 + 1 + i2], bl);
                  }
               }

               intss[l] = ints3;
            }
         }
      }

      return intss;
   }

   private static int blendColors(int i, int j, int k, int l, boolean bl) {
      return Mipmaps.alphaBlend(i, j, k, l);
   }

   public static void uploadTextureMipmap(int[][] ints, int i, int j, int k, int l, boolean bl, boolean bl2) {
      for (int m = 0; m < ints.length; m++) {
         int[] ints2 = ints[m];
         uploadTextureSub(m, ints2, i >> m, j >> m, k >> m, l >> m, bl, bl2, ints.length > 1);
      }
   }

   private static void uploadTextureSub(int i, int[] ints, int j, int k, int l, int m, boolean bl, boolean bl2, boolean bl3) {
      int n = 4194304 / j;
      setTextureBlurMipmap(bl, bl3);
      setTextureClamped(bl2);
      int i2 = 0;

      while (i2 < j * k) {
         int i3 = i2 / j;
         int i4 = Math.min(n, k - i3);
         int i5 = j * i4;
         copyToBufferPos(ints, i2, i5);
         GL11.glTexSubImage2D(3553, i, l, m + i3, j, i4, 32993, 33639, dataBuffer);
         i2 += j * i4;
      }
   }

   public static int uploadTextureImageAllocate(int i, BufferedImage bufferedImage, boolean bl, boolean bl2) {
      allocateTexture(i, bufferedImage.getWidth(), bufferedImage.getHeight());
      return uploadTextureImageSub(i, bufferedImage, 0, 0, bl, bl2);
   }

   public static void allocateTexture(int i, int j, int k) {
      allocateTextureImpl(i, 0, j, k);
   }

   public static void allocateTextureImpl(int i, int j, int k, int l) {
      Class<TextureUtil> class2 = TextureUtil.class;
      if (Reflector.SplashScreen.exists()) {
         class2 = Reflector.SplashScreen.getTargetClass();
      }

      synchronized (class2) {
         deleteTexture(i);
         bindTexture(i);
      }

      if (j >= 0) {
         GL11.glTexParameteri(3553, 33085, j);
         GL11.glTexParameterf(3553, 33082, 0.0F);
         GL11.glTexParameterf(3553, 33083, j);
         GL11.glTexParameterf(3553, 34049, 0.0F);
      }

      for (int m = 0; m <= j; m++) {
         GL11.glTexImage2D(3553, m, 6408, k >> m, l >> m, 0, 32993, 33639, (IntBuffer)null);
      }
   }

   public static int uploadTextureImageSub(int textureId, BufferedImage bufferedImage, int i, int j, boolean bl, boolean bl2) {
      bindTexture(textureId);
      uploadTextureImageSubImpl(bufferedImage, i, j, bl, bl2);
      return textureId;
   }

   private static void uploadTextureImageSubImpl(BufferedImage bufferedImage, int i, int j, boolean bl, boolean bl2) {
      int k = bufferedImage.getWidth();
      int l = bufferedImage.getHeight();
      int m = 4194304 / k;
      int[] ints = new int[m * k];
      setTextureBlurred(bl);
      setTextureClamped(bl2);

      for (int n = 0; n < k * l; n += k * m) {
         int i2 = n / k;
         int i3 = Math.min(m, l - i2);
         int i4 = k * i3;
         bufferedImage.getRGB(0, i2, k, i3, ints, 0, k);
         copyToBuffer(ints, i4);
         GL11.glTexSubImage2D(3553, 0, i, j + i2, k, i3, 32993, 33639, dataBuffer);
      }
   }

   public static void setTextureClamped(boolean bl) {
      if (bl) {
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
      } else {
         GL11.glTexParameteri(3553, 10242, 10497);
         GL11.glTexParameteri(3553, 10243, 10497);
      }
   }

   private static void setTextureBlurred(boolean bl) {
      setTextureBlurMipmap(bl, false);
   }

   public static void setTextureBlurMipmap(boolean bl, boolean bl2) {
      if (bl) {
         GL11.glTexParameteri(3553, 10241, bl2 ? 9987 : 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
      } else {
         int i = Config.getMipmapType();
         GL11.glTexParameteri(3553, 10241, bl2 ? i : 9728);
         GL11.glTexParameteri(3553, 10240, 9728);
      }
   }

   private static void copyToBuffer(int[] ints, int i) {
      copyToBufferPos(ints, 0, i);
   }

   private static void copyToBufferPos(int[] ints, int i, int j) {
      int[] ints2 = ints;
      if (Minecraft.getMinecraft().gameSettings.anaglyph) {
         ints2 = updateAnaglyph(ints);
      }

      ((Buffer)dataBuffer).clear();
      dataBuffer.put(ints2, i, j);
      ((Buffer)dataBuffer).position(0).limit(j);
   }

   public static void bindTexture(int i) {
      GlStateManager.bindTexture(i);
   }

   public static int[] readImageData(IResourceManager resourceManager, ResourceLocation imageLocation) throws IOException {
      BufferedImage bufferedImage = readBufferedImage(resourceManager.getResource(imageLocation).getInputStream());
      if (bufferedImage == null) {
         return null;
      }

      int i = bufferedImage.getWidth();
      int j = bufferedImage.getHeight();
      int[] ints = new int[i * j];
      bufferedImage.getRGB(0, 0, i, j, ints, 0, i);
      return ints;
   }

   public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException {
      if (imageStream == null) {
         return null;
      }

      BufferedImage bufferedImage;
      try {
         bufferedImage = ImageIO.read(imageStream);
      } finally {
         IOUtils.closeQuietly(imageStream);
      }

      return bufferedImage;
   }

   public static int[] updateAnaglyph(int[] ints) {
      int[] ints2 = new int[ints.length];

      for (int i = 0; i < ints.length; i++) {
         ints2[i] = anaglyphColor(ints[i]);
      }

      return ints2;
   }

   public static int anaglyphColor(int i) {
      int j = i >> 24 & 0xFF;
      int k = i >> 16 & 0xFF;
      int l = i >> 8 & 0xFF;
      int m = i & 0xFF;
      int n = (k * 30 + l * 59 + m * 11) / 100;
      int i2 = (k * 30 + l * 70) / 100;
      int i3 = (k * 30 + m * 70) / 100;
      return j << 24 | n << 16 | i2 << 8 | i3;
   }

   public static void saveGlTexture(String string, int i, int j, int k, int l) {
      bindTexture(i);
      GL11.glPixelStorei(3333, 1);
      GL11.glPixelStorei(3317, 1);

      for (int m = 0; m <= j; m++) {
         File file = new File(string + "_" + m + ".png");
         int n = k >> m;
         int i2 = l >> m;
         int i3 = n * i2;
         IntBuffer intBuffer = BufferUtils.createIntBuffer(i3);
         int[] ints = new int[i3];
         GL11.glGetTexImage(3553, m, 32993, 33639, intBuffer);
         intBuffer.get(ints);
         BufferedImage bufferedImage = new BufferedImage(n, i2, 2);
         bufferedImage.setRGB(0, 0, n, i2, ints, 0, n);

         try {
            ImageIO.write(bufferedImage, "png", file);
            logger.debug("Exported png to: {}", new Object[]{file.getAbsolutePath()});
         } catch (Exception exception) {
            logger.debug("Unable to write: ", exception);
         }
      }
   }

   public static void processPixelValues(int[] ints, int i, int j) {
      int[] ints2 = new int[i];
      int k = j / 2;

      for (int l = 0; l < k; l++) {
         System.arraycopy(ints, l * i, ints2, 0, i);
         System.arraycopy(ints, (j - 1 - l) * i, ints, l * i, i);
         System.arraycopy(ints2, 0, ints, (j - 1 - l) * i, i);
      }
   }

   static {
      int[] ints = new int[]{-524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040};
      int[] ints2 = new int[]{-16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
      int i = ints.length;

      for (int j = 0; j < 16; j++) {
         System.arraycopy(j < i ? ints : ints2, 0, missingTextureData, 16 * j, i);
         System.arraycopy(j < i ? ints2 : ints, 0, missingTextureData, 16 * j + i, i);
      }

      missingTexture.updateDynamicTexture();
      mipmapBuffer = new int[4];
   }
}
