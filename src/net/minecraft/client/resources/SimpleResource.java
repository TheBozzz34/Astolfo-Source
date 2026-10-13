package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class SimpleResource implements IResource {
   private final Map<String, IMetadataSection> mapMetadataSections = Maps.newHashMap();
   private final String resourcePackName;
   private final ResourceLocation srResourceLocation;
   private final InputStream resourceInputStream;
   private final InputStream mcmetaInputStream;
   private final IMetadataSerializer srMetadataSerializer;
   private boolean mcmetaJsonChecked;
   private JsonObject mcmetaJson;
   private static final long a = 120211013350037L;
   private static final long b = 4182245716107722783L;

   public SimpleResource(String var1, ResourceLocation var2, InputStream var3, InputStream var4, IMetadataSerializer var5) {
      this.resourcePackName = var1;
      this.srResourceLocation = var2;
      this.resourceInputStream = var3;
      this.mcmetaInputStream = var4;
      this.srMetadataSerializer = var5;
   }

   @Override
   public ResourceLocation getResourceLocation() {
      return this.srResourceLocation;
   }

   @Override
   public InputStream getInputStream() {
      return this.resourceInputStream;
   }

   @Override
   public boolean hasMetadata() {
      return this.mcmetaInputStream != null;
   }

   @Override
   public <T extends IMetadataSection> T getMetadata(String var1) {
      if (!this.hasMetadata()) {
         return null;
      }

      if (this.mcmetaJson == null && !this.mcmetaJsonChecked) {
         this.mcmetaJsonChecked = true;
         BufferedReader var5 = null;

         try {
            var5 = new BufferedReader(new InputStreamReader(this.mcmetaInputStream));
            this.mcmetaJson = new JsonParser().parse(var5).getAsJsonObject();
         } finally {
            IOUtils.closeQuietly(var5);
         }
      }

      IMetadataSection var9 = this.mapMetadataSections.get(var1);
      if (var9 == null) {
         var9 = this.srMetadataSerializer.parseMetadataSection(var1, this.mcmetaJson);
      }

      return (T)var9;
   }

   @Override
   public String getResourcePackName() {
      return this.resourcePackName;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof SimpleResource)) {
         return false;
      }

      SimpleResource var5 = (SimpleResource)var1;
      if (this.srResourceLocation != null) {
         if (!this.srResourceLocation.equals(var5.srResourceLocation)) {
            return false;
         }
      } else if (var5.srResourceLocation != null) {
         return false;
      }

      if (this.resourcePackName != null) {
         if (!this.resourcePackName.equals(var5.resourcePackName)) {
            return false;
         }
      } else if (var5.resourcePackName != null) {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode() {
      int var4 = this.resourcePackName != null ? this.resourcePackName.hashCode() : 0;
      return (int)b * var4 + (this.srResourceLocation != null ? this.srResourceLocation.hashCode() : 0);
   }
}
