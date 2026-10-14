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

   public SimpleShaderTexture(String texturePath) {
      this.texturePath = texturePath;
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      this.deleteGlTexture();
      InputStream inputStream = Shaders.getShaderPackResourceStream(this.texturePath);
      if (inputStream == null) {
         throw new FileNotFoundException("Shader texture not found: " + this.texturePath);
      }

      try {
         BufferedImage bufferedImage = TextureUtil.readBufferedImage(inputStream);
         TextureMetadataSection textureMetadataSection = this.loadTextureMetadataSection();
         TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedImage, textureMetadataSection.getTextureBlur(), textureMetadataSection.getTextureClamp());
      } finally {
         IOUtils.closeQuietly(inputStream);
      }
   }

   private TextureMetadataSection loadTextureMetadataSection() {
      String string = this.texturePath + ".mcmeta";
      String string2 = "texture";
      InputStream inputStream = Shaders.getShaderPackResourceStream(string);
      if (inputStream != null) {
         IMetadataSerializer iMetadataSerializer = METADATA_SERIALIZER;
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

         TextureMetadataSection textureMetadataSection;
         try {
            JsonObject jsonObject = new JsonParser().parse(bufferedReader).getAsJsonObject();
            TextureMetadataSection textureMetadataSection2 = iMetadataSerializer.parseMetadataSection(string2, jsonObject);
            if (textureMetadataSection2 == null) {
               return new TextureMetadataSection(false, false, new ArrayList<>());
            }

            textureMetadataSection = textureMetadataSection2;
         } catch (RuntimeException runtimeException) {
            SMCLog.warning("Error reading metadata: " + string);
            SMCLog.warning("" + runtimeException.getClass().getName() + ": " + runtimeException.getMessage());
            return new TextureMetadataSection(false, false, new ArrayList<>());
         } finally {
            IOUtils.closeQuietly(bufferedReader);
            IOUtils.closeQuietly(inputStream);
         }

         return textureMetadataSection;
      } else {
         return new TextureMetadataSection(false, false, new ArrayList<>());
      }
   }

   private static IMetadataSerializer makeMetadataSerializer() {
      IMetadataSerializer ms = new IMetadataSerializer();
      ms.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
      ms.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
      ms.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
      ms.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
      ms.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
      return ms;
   }
}
