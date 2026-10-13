package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredTexture extends AbstractTexture {
   private static final Logger logger = LogManager.getLogger();
   public final List<String> layeredTextureNames;
   private static final long a = 64104872767130L;

   public LayeredTexture(String... var1) {
      this.layeredTextureNames = Lists.newArrayList(var1);
   }

   @Override
   public void loadTexture(IResourceManager var1) throws IOException {
      this.deleteGlTexture();
      BufferedImage var5 = null;

      try {
         for (String var7 : this.layeredTextureNames) {
            if (var7 != null) {
               InputStream var8 = var1.getResource(new ResourceLocation(var7)).getInputStream();
               BufferedImage var9 = TextureUtil.readBufferedImage(var8);
               if (var5 == null) {
                  var5 = new BufferedImage(var9.getWidth(), var9.getHeight(), 2);
               }

               var5.getGraphics().drawImage(var9, 0, 0, (ImageObserver)null);
            }
         }
      } catch (IOException var10) {
         logger.error("Couldn't load layered image", var10);
         return;
      }

      TextureUtil.uploadTextureImage(this.getGlTextureId(), var5);
   }
}
