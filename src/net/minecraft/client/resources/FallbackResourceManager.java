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

   public FallbackResourceManager(IMetadataSerializer frmMetadataSerializerIn) {
      this.frmMetadataSerializer = frmMetadataSerializerIn;
   }

   public void addResourcePack(IResourcePack resourcePack) {
      this.resourcePacks.add(resourcePack);
   }

   @Override
   public Set<String> getResourceDomains() {
      return null;
   }

   @Override
   public IResource getResource(ResourceLocation location) throws IOException {
      IResourcePack iResourcePack = null;
      ResourceLocation resourceLocation = getLocationMcmeta(location);

      for (int i = this.resourcePacks.size() - 1; i >= 0; i--) {
         IResourcePack iResourcePack2 = this.resourcePacks.get(i);
         if (iResourcePack == null && iResourcePack2.resourceExists(resourceLocation)) {
            iResourcePack = iResourcePack2;
         }

         if (iResourcePack2.resourceExists(location)) {
            InputStream inputStream = null;
            if (iResourcePack != null) {
               inputStream = this.getInputStream(resourceLocation, iResourcePack);
            }

            return new SimpleResource(iResourcePack2.getPackName(), location, this.getInputStream(location, iResourcePack2), inputStream, this.frmMetadataSerializer);
         }
      }

      throw new FileNotFoundException(location.toString());
   }

   public InputStream getInputStream(ResourceLocation location, IResourcePack resourcePack) throws IOException {
      InputStream inputStream = resourcePack.getInputStream(location);
      return logger.isDebugEnabled() ? new FallbackResourceManager.InputStreamLeakedResourceLogger(inputStream, location, resourcePack.getPackName()) : inputStream;
   }

   @Override
   public List<IResource> getAllResources(ResourceLocation location) throws IOException {
      ArrayList arrayList = Lists.newArrayList();
      ResourceLocation resourceLocation = getLocationMcmeta(location);

      for (IResourcePack iResourcePack : this.resourcePacks) {
         if (iResourcePack.resourceExists(location)) {
            InputStream inputStream = iResourcePack.resourceExists(resourceLocation) ? this.getInputStream(resourceLocation, iResourcePack) : null;
            arrayList.add(new SimpleResource(iResourcePack.getPackName(), location, this.getInputStream(location, iResourcePack), inputStream, this.frmMetadataSerializer));
         }
      }

      if (arrayList.isEmpty()) {
         throw new FileNotFoundException(location.toString());
      } else {
         return arrayList;
      }
   }

   public static ResourceLocation getLocationMcmeta(ResourceLocation location) {
      return new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".mcmeta");
   }

   public static class InputStreamLeakedResourceLogger extends InputStream {
      private final InputStream inputStream;
      private final String message;
      private boolean isClosed = false;

      public InputStreamLeakedResourceLogger(InputStream inputStream, ResourceLocation location, String resourcePack) {
         this.inputStream = inputStream;
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         new Exception().printStackTrace(new PrintStream(byteArrayOutputStream));
         this.message = "Leaked resource: '" + location + "' loaded from pack: '" + resourcePack + "'\n" + byteArrayOutputStream.toString();
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
