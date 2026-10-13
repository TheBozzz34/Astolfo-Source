package net.minecraft.src;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import javax.imageio.ImageIO;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class TextureAnimations {
   private static TextureAnimation[] textureAnimations = null;
   private static final long a = 47223302926947L;

   public static void n() {
      textureAnimations = null;
   }

   public static void f() {
      textureAnimations = null;
      IResourcePack[] var2 = Config.getResourcePacks();
      textureAnimations = getTextureAnimations(var2);
      if (Config.pN()) {
         i();
      }
   }

   public static void s() {
      if (textureAnimations != null && Config.pN()) {
         i();
      }
   }

   public static void i() {
      if (textureAnimations != null) {
         for (int var3 = 0; var3 < textureAnimations.length; var3++) {
            TextureAnimation var4 = textureAnimations[var3];
            var4.updateTexture();
         }
      }
   }

   public static TextureAnimation[] getTextureAnimations(IResourcePack[] var0) {
      ArrayList var4 = new ArrayList();

      for (int var5 = 0; var5 < var0.length; var5++) {
         IResourcePack var6 = var0[var5];
         TextureAnimation[] var7 = getTextureAnimations(var6);
         if (var7 != null) {
            var4.addAll(Arrays.asList(var7));
         }
      }

      return (net.minecraft.src.TextureAnimation[])(var4.toArray(new TextureAnimation[var4.size()]));
   }

   public static TextureAnimation[] getTextureAnimations(IResourcePack var0) {
      String[] var4 = ResUtils.collectFiles(var0, "mcpatcher/anim", ".properties", (String[])null);
      if (var4.length <= 0) {
         return null;
      }

      ArrayList var5 = new ArrayList();

      for (int var6 = 0; var6 < var4.length; var6++) {
         String var7 = var4[var6];
         Config.dbg("Texture animation: " + var7);

         try {
            ResourceLocation var8 = new ResourceLocation(var7);
            InputStream var9 = var0.getInputStream(var8);
            Properties var10 = new Properties();
            var10.load(var9);
            TextureAnimation var11 = makeTextureAnimation(var10, var8);
            if (var11 != null) {
               ResourceLocation var12 = new ResourceLocation(var11.getDstTex());
               if (Config.getDefiningResourcePack(var12) != var0) {
                  Config.dbg("Skipped: " + var7 + ", target texture not loaded from same resource pack");
               } else {
                  var5.add(var11);
               }
            }
         } catch (FileNotFoundException var13) {
            Config.warn("File not found: " + var13.getMessage());
         } catch (IOException var14) {
            var14.printStackTrace();
         }
      }

      return (net.minecraft.src.TextureAnimation[])(var5.toArray(new TextureAnimation[var5.size()]));
   }

   public static TextureAnimation makeTextureAnimation(Properties var0, ResourceLocation var1) {
      String var5 = var0.getProperty("from");
      String var6 = var0.getProperty("to");
      int var7 = Config.parseInt(var0.getProperty("x"), -1);
      int var8 = Config.parseInt(var0.getProperty("y"), -1);
      int var9 = Config.parseInt(var0.getProperty("w"), -1);
      int var10 = Config.parseInt(var0.getProperty("h"), -1);
      if (var5 == null || var6 == null) {
         Config.warn("TextureAnimation: Source or target texture not specified");
         return null;
      }

      if (var7 >= 0 && var8 >= 0 && var9 >= 0 && var10 >= 0) {
         var5 = var5.trim();
         var6 = var6.trim();
         String var11 = TextureUtils.getBasePath(var1.getResourcePath());
         var5 = TextureUtils.fixResourcePath(var5, var11);
         var6 = TextureUtils.fixResourcePath(var6, var11);
         byte[] var12 = getCustomTextureData(var5, var9);
         if (var12 == null) {
            Config.warn("TextureAnimation: Source texture not found: " + var6);
            return null;
         } else {
            ResourceLocation var13 = new ResourceLocation(var6);
            if (!Config.hasResource(var13)) {
               Config.warn("TextureAnimation: Target texture not found: " + var6);
               return null;
            } else {
               return new TextureAnimation(var5, var12, var6, var13, var7, var8, var9, var10, var0, 1);
            }
         }
      } else {
         Config.warn("TextureAnimation: Invalid coordinates");
         return null;
      }
   }

   public static byte[] getCustomTextureData(String var0, int var1) {
      byte[] var5 = loadImage(var0, var1);
      if (var5 == null) {
         var5 = loadImage("/anim" + var0, var1);
      }

      return var5;
   }

   private static byte[] loadImage(String var0, int var1) {
      GameSettings var5 = Config.getGameSettings();

      try {
         ResourceLocation var6 = new ResourceLocation(var0);
         InputStream var7 = Config.getResourceStream(var6);
         if (var7 == null) {
            return null;
         }

         BufferedImage var8 = readTextureImage(var7);
         var7.close();
         if (var8 == null) {
            return null;
         }

         if (var1 > 0 && var8.getWidth() != var1) {
            double var9 = var8.getHeight() / var8.getWidth();
            int var11 = (int)(var1 * var9);
            var8 = scaleBufferedImage(var8, var1, var11);
         }

         int var23 = var8.getWidth();
         int var10 = var8.getHeight();
         int[] var24 = new int[var23 * var10];
         byte[] var12 = new byte[var23 * var10 * 4];
         var8.getRGB(0, 0, var23, var10, var24, 0, var23);

         for (int var13 = 0; var13 < var24.length; var13++) {
            int var14 = var24[var13] >> 24 & 0xFF;
            int var15 = var24[var13] >> 16 & 0xFF;
            int var16 = var24[var13] >> 8 & 0xFF;
            int var17 = var24[var13] & 0xFF;
            if (var5 != null && var5.anaglyph) {
               int var18 = (var15 * 30 + var16 * 59 + var17 * 11) / 100;
               int var19 = (var15 * 30 + var16 * 70) / 100;
               int var20 = (var15 * 30 + var17 * 70) / 100;
               var15 = var18;
               var16 = var19;
               var17 = var20;
            }

            var12[var13 * 4 + 0] = (byte)var15;
            var12[var13 * 4 + 1] = (byte)var16;
            var12[var13 * 4 + 2] = (byte)var17;
            var12[var13 * 4 + 3] = (byte)var14;
         }

         return var12;
      } catch (FileNotFoundException var21) {
         return null;
      } catch (Exception var22) {
         var22.printStackTrace();
         return null;
      }
   }

   private static BufferedImage readTextureImage(InputStream var0) throws IOException {
      BufferedImage var1 = ImageIO.read(var0);
      var0.close();
      return var1;
   }

   public static BufferedImage scaleBufferedImage(BufferedImage var0, int var1, int var2) {
      BufferedImage var3 = new BufferedImage(var1, var2, 2);
      Graphics2D var4 = var3.createGraphics();
      var4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      var4.drawImage(var0, 0, 0, var1, var2, (ImageObserver)null);
      return var3;
   }
}
