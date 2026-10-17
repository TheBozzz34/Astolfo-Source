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

   public static void reset() {
      textureAnimations = null;
   }

   public static void update() {
      textureAnimations = null;
      IResourcePack[] iResourcePacks = Config.getResourcePacks();
      textureAnimations = getTextureAnimations(iResourcePacks);
      if (Config.isAnimatedTextures()) {
         updateAnimations();
      }
   }

   public static void updateCustomAnimations() {
      if (textureAnimations != null && Config.isAnimatedTextures()) {
         updateAnimations();
      }
   }

   public static void updateAnimations() {
      if (textureAnimations != null) {
         for (int i = 0; i < textureAnimations.length; i++) {
            TextureAnimation textureAnimation = textureAnimations[i];
            textureAnimation.updateTexture();
         }
      }
   }

   public static TextureAnimation[] getTextureAnimations(IResourcePack[] rps) {
      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < rps.length; i++) {
         IResourcePack iResourcePack = rps[i];
         TextureAnimation[] textureAnimations = getTextureAnimations(iResourcePack);
         if (textureAnimations != null) {
            arrayList.addAll(Arrays.asList(textureAnimations));
         }
      }

      return (net.minecraft.src.TextureAnimation[])(arrayList.toArray(new TextureAnimation[arrayList.size()]));
   }

   public static TextureAnimation[] getTextureAnimations(IResourcePack rp) {
      String[] strings = ResUtils.collectFiles(rp, "mcpatcher/anim", ".properties", (String[])null);
      if (strings.length <= 0) {
         return null;
      }

      ArrayList arrayList = new ArrayList();

      for (int i = 0; i < strings.length; i++) {
         String string = strings[i];
         Config.dbg("Texture animation: " + string);

         try {
            ResourceLocation resourceLocation = new ResourceLocation(string);
            InputStream inputStream = rp.getInputStream(resourceLocation);
            Properties properties2 = new Properties();
            properties2.load(inputStream);
            TextureAnimation textureAnimation = makeTextureAnimation(properties2, resourceLocation);
            if (textureAnimation != null) {
               ResourceLocation resourceLocation2 = new ResourceLocation(textureAnimation.getDstTex());
               if (Config.getDefiningResourcePack(resourceLocation2) != rp) {
                  Config.dbg("Skipped: " + string + ", target texture not loaded from same resource pack");
               } else {
                  arrayList.add(textureAnimation);
               }
            }
         } catch (FileNotFoundException fileNotFoundException) {
            Config.warn("File not found: " + fileNotFoundException.getMessage());
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      }

      return (net.minecraft.src.TextureAnimation[])(arrayList.toArray(new TextureAnimation[arrayList.size()]));
   }

   public static TextureAnimation makeTextureAnimation(Properties props, ResourceLocation propLoc) {
      String string = props.getProperty("from");
      String string2 = props.getProperty("to");
      int i = Config.parseInt(props.getProperty("x"), -1);
      int j = Config.parseInt(props.getProperty("y"), -1);
      int k = Config.parseInt(props.getProperty("w"), -1);
      int l = Config.parseInt(props.getProperty("h"), -1);
      if (string == null || string2 == null) {
         Config.warn("TextureAnimation: Source or target texture not specified");
         return null;
      }

      if (i >= 0 && j >= 0 && k >= 0 && l >= 0) {
         string = string.trim();
         string2 = string2.trim();
         String string3 = TextureUtils.getBasePath(propLoc.getResourcePath());
         string = TextureUtils.fixResourcePath(string, string3);
         string2 = TextureUtils.fixResourcePath(string2, string3);
         byte[] bytes = getCustomTextureData(string, k);
         if (bytes == null) {
            Config.warn("TextureAnimation: Source texture not found: " + string2);
            return null;
         } else {
            ResourceLocation resourceLocation = new ResourceLocation(string2);
            if (!Config.hasResource(resourceLocation)) {
               Config.warn("TextureAnimation: Target texture not found: " + string2);
               return null;
            } else {
               return new TextureAnimation(string, bytes, string2, resourceLocation, i, j, k, l, props, 1);
            }
         }
      } else {
         Config.warn("TextureAnimation: Invalid coordinates");
         return null;
      }
   }

   public static byte[] getCustomTextureData(String imagePath, int tileWidth) {
      byte[] bytes = loadImage(imagePath, tileWidth);
      if (bytes == null) {
         bytes = loadImage("/anim" + imagePath, tileWidth);
      }

      return bytes;
   }

   private static byte[] loadImage(String name, int targetWidth) {
      GameSettings gameSettings = Config.getGameSettings();

      try {
         ResourceLocation resourceLocation = new ResourceLocation(name);
         InputStream inputStream = Config.getResourceStream(resourceLocation);
         if (inputStream == null) {
            return null;
         }

         BufferedImage bufferedImage = readTextureImage(inputStream);
         inputStream.close();
         if (bufferedImage == null) {
            return null;
         }

         if (targetWidth > 0 && bufferedImage.getWidth() != targetWidth) {
            double d = bufferedImage.getHeight() / bufferedImage.getWidth();
            int i = (int)(targetWidth * d);
            bufferedImage = scaleBufferedImage(bufferedImage, targetWidth, i);
         }

         int j = bufferedImage.getWidth();
         int k = bufferedImage.getHeight();
         int[] ints = new int[j * k];
         byte[] bytes = new byte[j * k * 4];
         bufferedImage.getRGB(0, 0, j, k, ints, 0, j);

         for (int l = 0; l < ints.length; l++) {
            int m = ints[l] >> 24 & 0xFF;
            int n = ints[l] >> 16 & 0xFF;
            int i2 = ints[l] >> 8 & 0xFF;
            int i3 = ints[l] & 0xFF;
            if (gameSettings != null && gameSettings.anaglyph) {
               int i4 = (n * 30 + i2 * 59 + i3 * 11) / 100;
               int i5 = (n * 30 + i2 * 70) / 100;
               int i6 = (n * 30 + i3 * 70) / 100;
               n = i4;
               i2 = i5;
               i3 = i6;
            }

            bytes[l * 4 + 0] = (byte)n;
            bytes[l * 4 + 1] = (byte)i2;
            bytes[l * 4 + 2] = (byte)i3;
            bytes[l * 4 + 3] = (byte)m;
         }

         return bytes;
      } catch (FileNotFoundException fileNotFoundException) {
         return null;
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   private static BufferedImage readTextureImage(InputStream par1InputStream) throws IOException {
      BufferedImage bufferedImage = ImageIO.read(par1InputStream);
      par1InputStream.close();
      return bufferedImage;
   }

   public static BufferedImage scaleBufferedImage(BufferedImage image, int width, int height) {
      BufferedImage bufferedImage = new BufferedImage(width, height, 2);
      Graphics2D graphics2D = bufferedImage.createGraphics();
      graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      graphics2D.drawImage(image, 0, 0, width, height, (ImageObserver)null);
      return bufferedImage;
   }
}
