package shadersmod.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import org.apache.commons.io.IOUtils;
import shadersmod.common.SMCLog;

public class SimpleShaderTexture extends AbstractTexture {
   private String texturePath;
   private static final IMetadataSerializer METADATA_SERIALIZER = makeMetadataSerializer();
   private static final long a = 6042616228422L;

   public SimpleShaderTexture(String var1) {
      this.texturePath = var1;
   }

   @Override
   public void loadTexture(IResourceManager var1) throws IOException {
      this.deleteGlTexture();
      InputStream var5 = Shaders.getShaderPackResourceStream(this.texturePath);
      if (var5 == null) {
         throw new FileNotFoundException("Shader texture not found: " + this.texturePath);
      }

      try {
         BufferedImage var6 = TextureUtil.readBufferedImage(var5);
         TextureMetadataSection var7 = this.loadTextureMetadataSection();
         TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), var6, var7.getTextureBlur(), var7.getTextureClamp());
      } finally {
         IOUtils.closeQuietly(var5);
      }
   }

   private TextureMetadataSection loadTextureMetadataSection() {
      String var4 = this.texturePath + ".mcmeta";
      String var5 = "texture";
      InputStream var6 = Shaders.getShaderPackResourceStream(var4);
      if (var6 != null) {
         IMetadataSerializer var7 = METADATA_SERIALIZER;
         BufferedReader var8 = new BufferedReader(new InputStreamReader(var6));

         TextureMetadataSection var9;
         try {
            JsonObject var10 = new JsonParser().parse(var8).getAsJsonObject();
            TextureMetadataSection var11 = var7.parseMetadataSection(var5, var10);
            if (var11 == null) {
               return new TextureMetadataSection(false, false, new ArrayList<>());
            }

            var9 = var11;
         } catch (RuntimeException var16) {
            SMCLog.warning("Error reading metadata: " + var4);
            SMCLog.warning("" + var16.getClass().getName() + ": " + var16.getMessage());
            return new TextureMetadataSection(false, false, new ArrayList<>());
         } finally {
            IOUtils.closeQuietly(var8);
            IOUtils.closeQuietly(var6);
         }

         return var9;
      } else {
         return new TextureMetadataSection(false, false, new ArrayList<>());
      }
   }

   private static IMetadataSerializer makeMetadataSerializer() {
      IMetadataSerializer var0 = new IMetadataSerializer();
      var0.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
      var0.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
      var0.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
      var0.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
      var0.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
      return var0;
   }
}
