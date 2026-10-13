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
   private static final long a = 104233910743670L;

   public static int glGenTextures() {
      return GlStateManager.generateTexture();
   }

   public static void deleteTexture(int var0) {
      GlStateManager.deleteTexture(var0);
   }

   public static int uploadTextureImage(int var0, BufferedImage var1) {
      return uploadTextureImageAllocate(var0, var1, false, false);
   }

   public static void uploadTexture(int var0, int[] var1, int var2, int var3) {
      bindTexture(var0);
      uploadTextureSub(0, var1, var2, var3, 0, 0, false, false, false);
   }

   public static int[][] generateMipmapData(int var0, int var1, int[][] var2) {
      int[][] var6 = new int[var0 + 1][];
      var6[0] = var2[0];
      if (var0 > 0) {
         boolean var7 = false;

         for (int var8 = 0; var8 < var2.length; var8++) {
            if (var2[0][var8] >> 24 == 0) {
               var7 = true;
               break;
            }
         }

         for (int var17 = 1; var17 <= var0; var17++) {
            if (var2[var17] != null) {
               var6[var17] = var2[var17];
            } else {
               int[] var9 = var6[var17 - 1];
               int[] var10 = new int[var9.length >> 2];
               int var11 = var1 >> var17;
               int var12 = var10.length / var11;
               int var13 = var11 << 1;

               for (int var14 = 0; var14 < var11; var14++) {
                  for (int var15 = 0; var15 < var12; var15++) {
                     int var16 = 2 * (var14 + var15 * var13);
                     var10[var14 + var15 * var11] = blendColors(var9[var16 + 0], var9[var16 + 1], var9[var16 + 0 + var13], var9[var16 + 1 + var13], var7);
                  }
               }

               var6[var17] = var10;
            }
         }
      }

      return var6;
   }

   private static int blendColors(int var0, int var1, int var2, int var3, boolean var4) {
      return Mipmaps.alphaBlend(var0, var1, var2, var3);
   }

   public static void uploadTextureMipmap(int[][] var0, int var1, int var2, int var3, int var4, boolean var5, boolean var6) {
      for (int var10 = 0; var10 < var0.length; var10++) {
         int[] var11 = var0[var10];
         uploadTextureSub(var10, var11, var1 >> var10, var2 >> var10, var3 >> var10, var4 >> var10, var5, var6, var0.length > 1);
      }
   }

   private static void uploadTextureSub(int var0, int[] var1, int var2, int var3, int var4, int var5, boolean var6, boolean var7, boolean var8) {
      long var9 = a ^ 19012270978996L;
      int var11 = 4194304 / var2;
      setTextureBlurMipmap(var6, var8);
      setTextureClamped(var7);
      int var13 = 0;

      while (var13 < var2 * var3) {
         int var14 = var13 / var2;
         int var12 = Math.min(var11, var3 - var14);
         long var10000 = 2777398024165055802L ^ var9;
         int var15 = var2 * var12;
         copyToBufferPos(var1, var13, var15);
         GL11.glTexSubImage2D(3553, var0, var4, var5 + var14, var2, var12, 32993, 33639, dataBuffer);
         var13 += var2 * var12;
      }
   }

   public static int uploadTextureImageAllocate(int var0, BufferedImage var1, boolean var2, boolean var3) {
      allocateTexture(var0, var1.getWidth(), var1.getHeight());
      return uploadTextureImageSub(var0, var1, 0, 0, var2, var3);
   }

   public static void allocateTexture(int var0, int var1, int var2) {
      allocateTextureImpl(var0, 0, var1, var2);
   }

   public static void allocateTextureImpl(int var0, int var1, int var2, int var3) {
      Class<TextureUtil> var6 = TextureUtil.class;
      if (Reflector.SplashScreen.exists()) {
         var6 = Reflector.SplashScreen.getTargetClass();
      }

      synchronized (var6) {
         deleteTexture(var0);
         bindTexture(var0);
      }

      if (var1 >= 0) {
         GL11.glTexParameteri(3553, 33085, var1);
         GL11.glTexParameterf(3553, 33082, 0.0F);
         GL11.glTexParameterf(3553, 33083, var1);
         GL11.glTexParameterf(3553, 34049, 0.0F);
      }

      for (int var7 = 0; var7 <= var1; var7++) {
         GL11.glTexImage2D(3553, var7, 6408, var2 >> var7, var3 >> var7, 0, 32993, 33639, (IntBuffer)null);
      }
   }

   public static int uploadTextureImageSub(int var0, BufferedImage var1, int var2, int var3, boolean var4, boolean var5) {
      bindTexture(var0);
      uploadTextureImageSubImpl(var1, var2, var3, var4, var5);
      return var0;
   }

   private static void uploadTextureImageSubImpl(BufferedImage var0, int var1, int var2, boolean var3, boolean var4) {
      long var5 = a ^ 9968789475596L;
      int var8 = var0.getWidth();
      int var9 = var0.getHeight();
      int var10 = 4194304 / var8;
      int[] var11 = new int[var10 * var8];
      setTextureBlurred(var3);
      setTextureClamped(var4);

      for (int var12 = 0; var12 < var8 * var9; var12 += var8 * var10) {
         int var13 = var12 / var8;
         int var14 = Math.min(var10, var9 - var13);
         long var10000 = 2777406581509553026L ^ var5;
         int var15 = var8 * var14;
         var0.getRGB(0, var13, var8, var14, var11, 0, var8);
         copyToBuffer(var11, var15);
         GL11.glTexSubImage2D(3553, 0, var1, var2 + var13, var8, var14, 32993, 33639, dataBuffer);
      }
   }

   public static void setTextureClamped(boolean var0) {
      if (var0) {
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
      } else {
         GL11.glTexParameteri(3553, 10242, 10497);
         GL11.glTexParameteri(3553, 10243, 10497);
      }
   }

   private static void setTextureBlurred(boolean var0) {
      setTextureBlurMipmap(var0, false);
   }

   public static void setTextureBlurMipmap(boolean var0, boolean var1) {
      if (var0) {
         GL11.glTexParameteri(3553, 10241, var1 ? 9987 : 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
      } else {
         int var4 = Config.getMipmapType();
         GL11.glTexParameteri(3553, 10241, var1 ? var4 : 9728);
         GL11.glTexParameteri(3553, 10240, 9728);
      }
   }

   private static void copyToBuffer(int[] var0, int var1) {
      copyToBufferPos(var0, 0, var1);
   }

   private static void copyToBufferPos(int[] var0, int var1, int var2) {
      int[] var5 = var0;
      if (Minecraft.getMinecraft().gameSettings.anaglyph) {
         var5 = updateAnaglyph(var0);
      }

      ((Buffer)dataBuffer).clear();
      dataBuffer.put(var5, var1, var2);
      ((Buffer)dataBuffer).position(0).limit(var2);
   }

   public static void bindTexture(int var0) {
      GlStateManager.bindTexture(var0);
   }

   public static int[] readImageData(IResourceManager var0, ResourceLocation var1) throws IOException {
      BufferedImage var5 = readBufferedImage(var0.getResource(var1).getInputStream());
      if (var5 == null) {
         return null;
      }

      int var6 = var5.getWidth();
      int var7 = var5.getHeight();
      int[] var8 = new int[var6 * var7];
      var5.getRGB(0, 0, var6, var7, var8, 0, var6);
      return var8;
   }

   public static BufferedImage readBufferedImage(InputStream var0) throws IOException {
      if (var0 == null) {
         return null;
      }

      BufferedImage var4;
      try {
         var4 = ImageIO.read(var0);
      } finally {
         IOUtils.closeQuietly(var0);
      }

      return var4;
   }

   public static int[] updateAnaglyph(int[] var0) {
      int[] var3 = new int[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = anaglyphColor(var0[var4]);
      }

      return var3;
   }

   public static int anaglyphColor(int var0) {
      int var3 = var0 >> 24 & 0xFF;
      int var4 = var0 >> 16 & 0xFF;
      int var5 = var0 >> 8 & 0xFF;
      int var6 = var0 & 0xFF;
      int var7 = (var4 * 30 + var5 * 59 + var6 * 11) / 100;
      int var8 = (var4 * 30 + var5 * 70) / 100;
      int var9 = (var4 * 30 + var6 * 70) / 100;
      return var3 << 24 | var7 << 16 | var8 << 8 | var9;
   }

   public static void saveGlTexture(String var0, int var1, int var2, int var3, int var4) {
      long var5 = a ^ 127131821983565L;
      bindTexture(var1);
      GL11.glPixelStorei(3333, 1);
      GL11.glPixelStorei(3317, 1);

      for (int var8 = 0; var8 <= var2; var8++) {
         File var9 = new File(var0 + "_" + var8 + ".png");
         int var10 = var3 >> var8;
         long var10000 = 2777364323776097731L ^ var5;
         int var11 = var4 >> var8;
         int var12 = var10 * var11;
         IntBuffer var13 = BufferUtils.createIntBuffer(var12);
         int[] var14 = new int[var12];
         GL11.glGetTexImage(3553, var8, 32993, 33639, var13);
         var13.get(var14);
         BufferedImage var15 = new BufferedImage(var10, var11, 2);
         var15.setRGB(0, 0, var10, var11, var14, 0, var10);

         try {
            ImageIO.write(var15, "png", var9);
            logger.debug("Exported png to: {}", new Object[]{var9.getAbsolutePath()});
         } catch (Exception var17) {
            logger.debug("Unable to write: ", var17);
         }
      }
   }

   public static void processPixelValues(int[] var0, int var1, int var2) {
      int[] var6 = new int[var1];
      int var7 = var2 / 2;

      for (int var8 = 0; var8 < var7; var8++) {
         System.arraycopy(var0, var8 * var1, var6, 0, var1);
         System.arraycopy(var0, (var2 - 1 - var8) * var1, var0, var8 * var1, var1);
         System.arraycopy(var6, 0, var0, (var2 - 1 - var8) * var1, var1);
      }
   }

   static {
      long var20 = a ^ 122495466120457L;
      long var10000 = 8718222394202263368L ^ var20;
      int[] var24 = new int[]{-524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040};
      int[] var25 = new int[]{-16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
      int var26 = var24.length;

      for (int var27 = 0; var27 < 16; var27++) {
         System.arraycopy(var27 < var26 ? var24 : var25, 0, missingTextureData, 16 * var27, var26);
         System.arraycopy(var27 < var26 ? var25 : var24, 0, missingTextureData, 16 * var27 + var26, var26);
      }

      missingTexture.updateDynamicTexture();
      mipmapBuffer = new int[4];
   }
}
