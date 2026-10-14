package net.minecraft.client.resources;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleReloadableResourceManager implements IReloadableResourceManager {
   private static final Logger logger = LogManager.getLogger();
   private static final Joiner joinerResourcePacks = Joiner.on(", ");
   private final Map<String, FallbackResourceManager> domainResourceManagers = Maps.newHashMap();
   private final List<IResourceManagerReloadListener> reloadListeners = Lists.newArrayList();
   private final Set<String> setResourceDomains = Sets.newLinkedHashSet();
   private final IMetadataSerializer rmMetadataSerializer;

   public SimpleReloadableResourceManager(IMetadataSerializer rmMetadataSerializerIn) {
      this.rmMetadataSerializer = rmMetadataSerializerIn;
   }

   public void reloadResourcePack(IResourcePack resourcePack) {
      for (String string : resourcePack.getResourceDomains()) {
         this.setResourceDomains.add(string);
         FallbackResourceManager fallbackResourceManager = this.domainResourceManagers.get(string);
         if (fallbackResourceManager == null) {
            fallbackResourceManager = new FallbackResourceManager(this.rmMetadataSerializer);
            this.domainResourceManagers.put(string, fallbackResourceManager);
         }

         fallbackResourceManager.addResourcePack(resourcePack);
      }
   }

   @Override
   public Set<String> getResourceDomains() {
      return this.setResourceDomains;
   }

   @Override
   public IResource getResource(ResourceLocation location) throws IOException {
      IResourceManager iResourceManager = this.domainResourceManagers.get(location.getResourceDomain());
      if (iResourceManager != null) {
         return iResourceManager.getResource(location);
      } else {
         throw new FileNotFoundException(location.toString());
      }
   }

   @Override
   public List<IResource> getAllResources(ResourceLocation location) throws IOException {
      IResourceManager iResourceManager = this.domainResourceManagers.get(location.getResourceDomain());
      if (iResourceManager != null) {
         return iResourceManager.getAllResources(location);
      } else {
         throw new FileNotFoundException(location.toString());
      }
   }

   private void clearResources() {
      this.domainResourceManagers.clear();
      this.setResourceDomains.clear();
   }

   @Override
   public void reloadResources(List<IResourcePack> resourcesPacksList) {
      this.clearResources();
      logger.info("Reloading ResourceManager: " + joinerResourcePacks.join(Iterables.transform(resourcesPacksList, new Function<IResourcePack, String>() {
         public String apply(IResourcePack iResourcePack) {
            return iResourcePack.getPackName();
         }
      })));

      for (IResourcePack iResourcePack2 : resourcesPacksList) {
         this.reloadResourcePack(iResourcePack2);
      }

      this.notifyReloadListeners();
   }

   @Override
   public void registerReloadListener(IResourceManagerReloadListener reloadListener) {
      this.reloadListeners.add(reloadListener);
      reloadListener.onResourceManagerReload(this);
   }

   private void notifyReloadListeners() {
      for (IResourceManagerReloadListener iResourceManagerReloadListener : this.reloadListeners) {
         iResourceManagerReloadListener.onResourceManagerReload(this);
      }
   }
}
