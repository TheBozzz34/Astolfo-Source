package net.minecraft.client.resources;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractResourcePack implements IResourcePack {
   private static final Logger resourceLog = LogManager.getLogger();
   public final File resourcePackFile;
   private static final long a = 49127330098165L;

   public AbstractResourcePack(File var1) {
      this.resourcePackFile = var1;
   }

   private static String locationToName(ResourceLocation var0) {
      return String.format("%s/%s/%s", "assets", var0.getResourceDomain(), var0.getResourcePath());
   }

   public static String getRelativeName(File var0, File var1) {
      return var0.toURI().relativize(var1.toURI()).getPath();
   }

   @Override
   public InputStream getInputStream(ResourceLocation var1) throws IOException {
      return this.getInputStreamByName(locationToName(var1));
   }

   @Override
   public boolean resourceExists(ResourceLocation var1) {
      return this.hasResourceName(locationToName(var1));
   }

   public abstract InputStream getInputStreamByName(String var1) throws IOException;

   public abstract boolean hasResourceName(String var1);

   public void logNameNotLowercase(String var1) {
      resourceLog.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", var1, this.resourcePackFile);
   }

   @Override
   public <T extends IMetadataSection> T getPackMetadata(IMetadataSerializer var1, String var2) throws IOException {
      return readMetadata(var1, this.getInputStreamByName("pack.mcmeta"), var2);
   }

   public static <T extends IMetadataSection> T readMetadata(IMetadataSerializer var0, InputStream var1, String var2) {
      JsonObject var3 = null;
      BufferedReader var4 = null;

      try {
         var4 = new BufferedReader(new InputStreamReader(var1, Charsets.UTF_8));
         var3 = new JsonParser().parse(var4).getAsJsonObject();
      } catch (RuntimeException var9) {
         throw new JsonParseException(var9);
      } finally {
         IOUtils.closeQuietly(var4);
      }

      return var0.parseMetadataSection(var2, var3);
   }

   @Override
   public BufferedImage getPackImage() throws IOException {
      return TextureUtil.readBufferedImage(this.getInputStreamByName("pack.png"));
   }

   @Override
   public String getPackName() {
      return this.resourcePackFile.getName();
   }
}
