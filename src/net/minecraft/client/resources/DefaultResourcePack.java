package net.minecraft.client.resources;

import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.src.ReflectorForge;
import net.minecraft.util.ResourceLocation;

public class DefaultResourcePack implements IResourcePack {
   public static final Set<String> defaultResourceDomains = ImmutableSet.of("minecraft", "realms");
   private final Map<String, File> mapAssets;
   private static final long a = 6589187294271L;

   public DefaultResourcePack(Map<String, File> var1) {
      this.mapAssets = var1;
   }

   @Override
   public InputStream getInputStream(ResourceLocation var1) throws IOException {
      InputStream var5 = this.getResourceStream(var1);
      if (var5 != null) {
         return var5;
      } else {
         InputStream var6 = this.getInputStreamAssets(var1);
         if (var6 != null) {
            return var6;
         } else {
            throw new FileNotFoundException(var1.getResourcePath());
         }
      }
   }

   public InputStream getInputStreamAssets(ResourceLocation var1) throws IOException, FileNotFoundException {
      File var5 = this.mapAssets.get(var1.toString());
      return var5 != null && var5.isFile() ? new FileInputStream(var5) : null;
   }

   private InputStream getResourceStream(ResourceLocation var1) {
      String var5 = "/assets/" + var1.getResourceDomain() + "/" + var1.getResourcePath();
      InputStream var6 = ReflectorForge.getOptiFineResourceStream(var5);
      return var6 != null ? var6 : DefaultResourcePack.class.getResourceAsStream("/assets/" + var1.getResourceDomain() + "/" + var1.getResourcePath());
   }

   @Override
   public boolean resourceExists(ResourceLocation var1) {
      return this.getResourceStream(var1) != null || this.mapAssets.containsKey(var1.toString());
   }

   @Override
   public Set<String> getResourceDomains() {
      return defaultResourceDomains;
   }

   @Override
   public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer var1, String var2) throws IOException {
      try {
         FileInputStream var5 = new FileInputStream(this.mapAssets.get("pack.mcmeta"));
         return AbstractResourcePack.readMetadata(var1, var5, var2);
      } catch (RuntimeException var6) {
         return null;
      } catch (FileNotFoundException var7) {
         return null;
      }
   }

   @Override
   public BufferedImage getPackImage() throws IOException {
      return TextureUtil.readBufferedImage(DefaultResourcePack.class.getResourceAsStream("/" + new ResourceLocation("pack.png").getResourcePath()));
   }

   @Override
   public String getPackName() {
      return "Default";
   }
}
