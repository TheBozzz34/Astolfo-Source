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

   public LayeredTexture(String... textureNames) {
      this.layeredTextureNames = Lists.newArrayList(textureNames);
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();
      BufferedImage bufferedImage = null;

      try {
         for (String string : this.layeredTextureNames) {
            if (string != null) {
               InputStream inputStream = resourceManager.getResource(new ResourceLocation(string)).getInputStream();
               BufferedImage bufferedImage2 = TextureUtil.readBufferedImage(inputStream);
               if (bufferedImage == null) {
                  bufferedImage = new BufferedImage(bufferedImage2.getWidth(), bufferedImage2.getHeight(), 2);
               }

               bufferedImage.getGraphics().drawImage(bufferedImage2, 0, 0, (ImageObserver)null);
            }
         }
      } catch (IOException ioException) {
         logger.error("Couldn't load layered image", ioException);
         return;
      }

      TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedImage);
   }
}
