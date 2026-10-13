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
   private static final long a = 131742729215468L;

   public LayeredColorMaskTexture(ResourceLocation var1, List<String> var2, List<EnumDyeColor> var3) {
      this.textureLocation = var1;
      this.field_174949_h = var2;
      this.field_174950_i = var3;
   }

   @Override
   public void loadTexture(IResourceManager var1) throws IOException {
      this.deleteGlTexture();

      BufferedImage var5;
      try {
         BufferedImage var6 = TextureUtil.readBufferedImage(var1.getResource(this.textureLocation).getInputStream());
         int var7 = var6.getType();
         if (var7 == 0) {
            var7 = 6;
         }

         var5 = new BufferedImage(var6.getWidth(), var6.getHeight(), var7);
         Graphics var8 = var5.getGraphics();
         var8.drawImage(var6, 0, 0, (ImageObserver)null);

         for (int var9 = 0; var9 < 17 && var9 < this.field_174949_h.size() && var9 < this.field_174950_i.size(); var9++) {
            String var10 = this.field_174949_h.get(var9);
            MapColor var11 = this.field_174950_i.get(var9).getMapColor();
            if (var10 != null) {
               InputStream var12 = var1.getResource(new ResourceLocation(var10)).getInputStream();
               BufferedImage var13 = TextureUtil.readBufferedImage(var12);
               if (var13.getWidth() == var5.getWidth() && var13.getHeight() == var5.getHeight() && var13.getType() == 6) {
                  for (int var14 = 0; var14 < var13.getHeight(); var14++) {
                     for (int var15 = 0; var15 < var13.getWidth(); var15++) {
                        int var16 = var13.getRGB(var15, var14);
                        if ((var16 & 0xFF000000) != 0) {
                           int var17 = (var16 & 0xFF0000) << 8 & 0xFF000000;
                           int var18 = var6.getRGB(var15, var14);
                           int var19 = MathHelper.func_180188_d(var18, var11.colorValue) & 16777215;
                           var13.setRGB(var15, var14, var17 | var19);
                        }
                     }
                  }

                  var5.getGraphics().drawImage(var13, 0, 0, (ImageObserver)null);
               }
            }
         }
      } catch (IOException var20) {
         LOG.error("Couldn't load layered image", var20);
         return;
      }

      TextureUtil.uploadTextureImage(this.getGlTextureId(), var5);
   }
}
