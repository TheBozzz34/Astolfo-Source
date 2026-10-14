package net.minecraft.client.renderer.texture;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredColorMaskTexture extends AbstractTexture {
   private static final Logger LOG = LogManager.getLogger();
   private final ResourceLocation textureLocation;
   private final List<String> field_174949_h;
   private final List<EnumDyeColor> field_174950_i;

   public LayeredColorMaskTexture(ResourceLocation textureLocationIn, List<String> list, List<EnumDyeColor> list2) {
      this.textureLocation = textureLocationIn;
      this.field_174949_h = list;
      this.field_174950_i = list2;
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();

      BufferedImage bufferedImage;
      try {
         BufferedImage bufferedImage2 = TextureUtil.readBufferedImage(resourceManager.getResource(this.textureLocation).getInputStream());
         int i = bufferedImage2.getType();
         if (i == 0) {
            i = 6;
         }

         bufferedImage = new BufferedImage(bufferedImage2.getWidth(), bufferedImage2.getHeight(), i);
         Graphics graphics = bufferedImage.getGraphics();
         graphics.drawImage(bufferedImage2, 0, 0, (ImageObserver)null);

         for (int j = 0; j < 17 && j < this.field_174949_h.size() && j < this.field_174950_i.size(); j++) {
            String string = this.field_174949_h.get(j);
            MapColor mapColor = this.field_174950_i.get(j).getMapColor();
            if (string != null) {
               InputStream inputStream = resourceManager.getResource(new ResourceLocation(string)).getInputStream();
               BufferedImage bufferedImage3 = TextureUtil.readBufferedImage(inputStream);
               if (bufferedImage3.getWidth() == bufferedImage.getWidth() && bufferedImage3.getHeight() == bufferedImage.getHeight() && bufferedImage3.getType() == 6) {
                  for (int k = 0; k < bufferedImage3.getHeight(); k++) {
                     for (int l = 0; l < bufferedImage3.getWidth(); l++) {
                        int m = bufferedImage3.getRGB(l, k);
                        if ((m & 0xFF000000) != 0) {
                           int n = (m & 0xFF0000) << 8 & 0xFF000000;
                           int i2 = bufferedImage2.getRGB(l, k);
                           int i3 = MathHelper.func_180188_d(i2, mapColor.colorValue) & 16777215;
                           bufferedImage3.setRGB(l, k, n | i3);
                        }
                     }
                  }

                  bufferedImage.getGraphics().drawImage(bufferedImage3, 0, 0, (ImageObserver)null);
               }
            }
         }
      } catch (IOException ioException) {
         LOG.error("Couldn't load layered image", ioException);
         return;
      }

      TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedImage);
   }
}
