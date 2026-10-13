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
   private static final long a = 73029731538372L;

   public SimpleReloadableResourceManager(IMetadataSerializer var1) {
      this.rmMetadataSerializer = var1;
   }

   public void reloadResourcePack(IResourcePack var1) {
      for (String var6 : var1.getResourceDomains()) {
         this.setResourceDomains.add(var6);
         FallbackResourceManager var7 = this.domainResourceManagers.get(var6);
         if (var7 == null) {
            var7 = new FallbackResourceManager(this.rmMetadataSerializer);
            this.domainResourceManagers.put(var6, var7);
         }

         var7.addResourcePack(var1);
      }
   }

   @Override
   public Set<String> getResourceDomains() {
      return this.setResourceDomains;
   }

   @Override
   public IResource getResource(ResourceLocation var1) throws IOException {
      IResourceManager var5 = this.domainResourceManagers.get(var1.getResourceDomain());
      if (var5 != null) {
         return var5.getResource(var1);
      } else {
         throw new FileNotFoundException(var1.toString());
      }
   }

   @Override
   public List<IResource> getAllResources(ResourceLocation var1) throws IOException {
      IResourceManager var5 = this.domainResourceManagers.get(var1.getResourceDomain());
      if (var5 != null) {
         return var5.getAllResources(var1);
      } else {
         throw new FileNotFoundException(var1.toString());
      }
   }

   private void clearResources() {
      this.domainResourceManagers.clear();
      this.setResourceDomains.clear();
   }

   @Override
   public void reloadResources(List<IResourcePack> var1) {
      this.clearResources();
      logger.info("Reloading ResourceManager: " + joinerResourcePacks.join(Iterables.transform(var1, new Function<IResourcePack, String>() {
         public String apply(IResourcePack var1) {
            return var1.getPackName();
         }
      })));

      for (IResourcePack var6 : var1) {
         this.reloadResourcePack(var6);
      }

      this.notifyReloadListeners();
   }

   @Override
   public void registerReloadListener(IResourceManagerReloadListener var1) {
      this.reloadListeners.add(var1);
      var1.onResourceManagerReload(this);
   }

   private void notifyReloadListeners() {
      for (IResourceManagerReloadListener var5 : this.reloadListeners) {
         var5.onResourceManagerReload(this);
      }
   }
}
