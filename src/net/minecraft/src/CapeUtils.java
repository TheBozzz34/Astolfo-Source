package net.minecraft.src;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

public class CapeUtils {
   public static void downloadCape(AbstractClientPlayer player) {
      String string = player.getNameClear();
      if (string != null && !string.isEmpty()) {
         String string2 = "http://s.optifine.net/capes/" + string + ".png";
         String string3 = FilenameUtils.getBaseName(string2);
         ResourceLocation resourceLocation = new ResourceLocation("capeof/" + string3);
         TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
         ITextureObject iTextureObject = textureManager.getTexture(resourceLocation);
         if (iTextureObject != null && iTextureObject instanceof ThreadDownloadImageData) {
            ThreadDownloadImageData threadDownloadImageData = (ThreadDownloadImageData)iTextureObject;
            if (threadDownloadImageData.imageFound != null) {
               if (threadDownloadImageData.imageFound) {
                  player.setLocationOfCape(resourceLocation);
               }

               return;
            }
         }

         CapeImageBuffer capeImageBuffer = new CapeImageBuffer(player, resourceLocation);
         ThreadDownloadImageData threadDownloadImageData2 = new ThreadDownloadImageData((File)null, string2, (ResourceLocation)null, capeImageBuffer);
         threadDownloadImageData2.pipeline = true;
         textureManager.loadTexture(resourceLocation, threadDownloadImageData2);
      }
   }

   public static BufferedImage parseCape(BufferedImage img) {
      byte b = 32;
      byte b2 = 64;
      int i = img.getHeight();

      for (int j = img.getWidth(); b2 < j || b < i; b2 *= 2) {
         b *= 2;
      }

      BufferedImage bufferedImage = new BufferedImage(b2, b, 2);
      Graphics graphics = bufferedImage.getGraphics();
      graphics.drawImage(img, 0, 0, null);
      graphics.dispose();
      return bufferedImage;
   }
}
