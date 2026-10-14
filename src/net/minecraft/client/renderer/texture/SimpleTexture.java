package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shadersmod.client.ShadersTex;

public class SimpleTexture extends AbstractTexture {
   private static final Logger logger = LogManager.getLogger();
   public final ResourceLocation textureLocation;

   public SimpleTexture(ResourceLocation textureResourceLocation) {
      this.textureLocation = textureResourceLocation;
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();
      InputStream inputStream = null;

      try {
         IResource iResource = resourceManager.getResource(this.textureLocation);
         inputStream = iResource.getInputStream();
         BufferedImage bufferedImage = TextureUtil.readBufferedImage(inputStream);
         boolean bl = false;
         boolean bl2 = false;
         if (iResource.hasMetadata()) {
            try {
               TextureMetadataSection textureMetadataSection = iResource.getMetadata("texture");
               if (textureMetadataSection != null) {
                  bl = textureMetadataSection.getTextureBlur();
                  bl2 = textureMetadataSection.getTextureClamp();
               }
            } catch (RuntimeException runtimeException) {
               logger.warn("Failed reading metadata of: " + this.textureLocation, runtimeException);
            }
         }

         if (Config.isShaders()) {
            ShadersTex.loadSimpleTexture(this.getGlTextureId(), bufferedImage, bl, bl2, resourceManager, this.textureLocation, this.getMultiTexID());
         } else {
            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedImage, bl, bl2);
         }
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }
}
