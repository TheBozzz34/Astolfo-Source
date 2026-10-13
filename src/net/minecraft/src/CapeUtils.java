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
   private static final long a = 33570160743770L;

   public static void downloadCape(AbstractClientPlayer var0) {
      String var4 = var0.getNameClear();
      if (var4 != null && !var4.isEmpty()) {
         String var5 = "http://s.optifine.net/capes/" + var4 + ".png";
         String var6 = FilenameUtils.getBaseName(var5);
         ResourceLocation var7 = new ResourceLocation("capeof/" + var6);
         TextureManager var8 = Minecraft.getMinecraft().getTextureManager();
         ITextureObject var9 = var8.getTexture(var7);
         if (var9 != null && var9 instanceof ThreadDownloadImageData) {
            ThreadDownloadImageData var10 = (ThreadDownloadImageData)var9;
            if (var10.imageFound != null) {
               if (var10.imageFound) {
                  var0.setLocationOfCape(var7);
               }

               return;
            }
         }

         CapeImageBuffer var12 = new CapeImageBuffer(var0, var7);
         ThreadDownloadImageData var11 = new ThreadDownloadImageData((File)null, var5, (ResourceLocation)null, var12);
         var11.pipeline = true;
         var8.loadTexture(var7, var11);
      }
   }

   public static BufferedImage parseCape(BufferedImage var0) {
      byte var4 = 32;
      byte var5 = 64;
      int var6 = var0.getHeight();

      for (int var7 = var0.getWidth(); var5 < var7 || var4 < var6; var5 *= 2) {
         var4 *= 2;
      }

      BufferedImage var8 = new BufferedImage(var5, var4, 2);
      Graphics var9 = var8.getGraphics();
      var9.drawImage(var0, 0, 0, null);
      var9.dispose();
      return var8;
   }
}
