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
   private static final long b = 116934800512870L;

   public SimpleTexture(ResourceLocation var1) {
      this.textureLocation = var1;
   }

   @Override
   public void loadTexture(IResourceManager var1) throws IOException {
      this.deleteGlTexture();
      InputStream var5 = null;

      try {
         IResource var6 = var1.getResource(this.textureLocation);
         var5 = var6.getInputStream();
         BufferedImage var7 = TextureUtil.readBufferedImage(var5);
         boolean var8 = false;
         boolean var9 = false;
         if (var6.hasMetadata()) {
            try {
               TextureMetadataSection var10 = var6.getMetadata("texture");
               if (var10 != null) {
                  var8 = var10.getTextureBlur();
                  var9 = var10.getTextureClamp();
               }
            } catch (RuntimeException var14) {
               logger.warn("Failed reading metadata of: " + this.textureLocation, var14);
            }
         }

         if (Config.isShaders()) {
            ShadersTex.loadSimpleTexture(this.getGlTextureId(), var7, var8, var9, var1, this.textureLocation, this.getMultiTexID());
         } else {
            TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), var7, var8, var9);
         }
      } finally {
         if (var5 != null) {
            var5.close();
         }
      }
   }
}
