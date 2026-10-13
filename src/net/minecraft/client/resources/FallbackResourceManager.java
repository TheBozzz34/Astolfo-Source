package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements IResourceManager {
   private static final Logger logger = LogManager.getLogger();
   public final List<IResourcePack> resourcePacks = Lists.newArrayList();
   private final IMetadataSerializer frmMetadataSerializer;
   private static final long a = 737895431508L;

   public FallbackResourceManager(IMetadataSerializer var1) {
      this.frmMetadataSerializer = var1;
   }

   public void addResourcePack(IResourcePack var1) {
      this.resourcePacks.add(var1);
   }

   @Override
   public Set<String> getResourceDomains() {
      return null;
   }

   @Override
   public IResource getResource(ResourceLocation var1) throws IOException {
      IResourcePack var5 = null;
      ResourceLocation var6 = getLocationMcmeta(var1);

      for (int var7 = this.resourcePacks.size() - 1; var7 >= 0; var7--) {
         IResourcePack var8 = this.resourcePacks.get(var7);
         if (var5 == null && var8.resourceExists(var6)) {
            var5 = var8;
         }

         if (var8.resourceExists(var1)) {
            InputStream var9 = null;
            if (var5 != null) {
               var9 = this.getInputStream(var6, var5);
            }

            return new SimpleResource(var8.getPackName(), var1, this.getInputStream(var1, var8), var9, this.frmMetadataSerializer);
         }
      }

      throw new FileNotFoundException(var1.toString());
   }

   public InputStream getInputStream(ResourceLocation var1, IResourcePack var2) throws IOException {
      InputStream var5 = var2.getInputStream(var1);
      return logger.isDebugEnabled() ? new FallbackResourceManager.InputStreamLeakedResourceLogger(var5, var1, var2.getPackName()) : var5;
   }

   @Override
   public List<IResource> getAllResources(ResourceLocation var1) throws IOException {
      ArrayList var5 = Lists.newArrayList();
      ResourceLocation var6 = getLocationMcmeta(var1);

      for (IResourcePack var8 : this.resourcePacks) {
         if (var8.resourceExists(var1)) {
            InputStream var9 = var8.resourceExists(var6) ? this.getInputStream(var6, var8) : null;
            var5.add(new SimpleResource(var8.getPackName(), var1, this.getInputStream(var1, var8), var9, this.frmMetadataSerializer));
         }
      }

      if (var5.isEmpty()) {
         throw new FileNotFoundException(var1.toString());
      } else {
         return var5;
      }
   }

   public static ResourceLocation getLocationMcmeta(ResourceLocation var0) {
      return new ResourceLocation(var0.getResourceDomain(), var0.getResourcePath() + ".mcmeta");
   }

   public static class InputStreamLeakedResourceLogger extends InputStream {
      private final InputStream inputStream;
      private final String message;
      private boolean isClosed = false;
      private static final long a = 112383255152254L;

      public InputStreamLeakedResourceLogger(InputStream var1, ResourceLocation var2, String var3) {
         this.inputStream = var1;
         ByteArrayOutputStream var6 = new ByteArrayOutputStream();
         new Exception().printStackTrace(new PrintStream(var6));
         this.message = "Leaked resource: '" + var2 + "' loaded from pack: '" + var3 + "'\n" + var6.toString();
      }

      @Override
      public void close() throws IOException {
         this.inputStream.close();
         this.isClosed = true;
      }

      @Override
      public void finalize() throws Throwable {
         if (!this.isClosed) {
            FallbackResourceManager.logger.warn(this.message);
         }

         super.finalize();
      }

      @Override
      public int read() throws IOException {
         return this.inputStream.read();
      }
   }
}
