package net.minecraft.client.resources;

import bypass.Astolfo;
import bypass.event.network.ResourcePackEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackRepository {
   private static final Logger logger = LogManager.getLogger();
   private static final FileFilter resourcePackFilter = new FileFilter() {
      private static final long a = 27413602215353L;

      @Override
      public boolean accept(File var1) {
         boolean var5 = var1.isFile() && var1.getName().endsWith(".zip");
         boolean var6 = var1.isDirectory() && new File(var1, "pack.mcmeta").isFile();
         return var5 || var6;
      }
   };
   private final File dirResourcepacks;
   public final IResourcePack rprDefaultResourcePack;
   private final File dirServerResourcepacks;
   public final IMetadataSerializer rprMetadataSerializer;
   private IResourcePack resourcePackInstance;
   private final ReentrantLock lock = new ReentrantLock();
   private ListenableFuture<Object> downloadingPacks;
   private List<ResourcePackRepository.Entry> repositoryEntriesAll = Lists.newArrayList();
   private List<ResourcePackRepository.Entry> repositoryEntries = Lists.newArrayList();
   private static final long b = 37279927914059L;

   public ResourcePackRepository(File var1, File var2, IResourcePack var3, IMetadataSerializer var4, GameSettings var5) {
      this.dirResourcepacks = var1;
      this.dirServerResourcepacks = var2;
      this.rprDefaultResourcePack = var3;
      this.rprMetadataSerializer = var4;
      this.fixDirResourcepacks();
      this.updateRepositoryEntriesAll();
      Iterator var9 = var5.resourcePacks.iterator();

      while (var9.hasNext()) {
         String var10 = (String)var9.next();

         for (ResourcePackRepository.Entry var12 : this.repositoryEntriesAll) {
            if (var12.getResourcePackName().equals(var10)) {
               if (var12.func_183027_f() == 1 || var5.incompatibleResourcePacks.contains(var12.getResourcePackName())) {
                  this.repositoryEntries.add(var12);
                  break;
               }

               var9.remove();
               logger.warn("Removed selected resource pack {} because it's no longer compatible", new Object[]{var12.getResourcePackName()});
            }
         }
      }
   }

   private void fixDirResourcepacks() {
      if (this.dirResourcepacks.exists()) {
         if (!this.dirResourcepacks.isDirectory() && (!this.dirResourcepacks.delete() || !this.dirResourcepacks.mkdirs())) {
            logger.warn("Unable to recreate resourcepack folder, it exists but is not a directory: " + this.dirResourcepacks);
         }
      } else if (!this.dirResourcepacks.mkdirs()) {
         logger.warn("Unable to create resourcepack folder: " + this.dirResourcepacks);
      }
   }

   private List<File> getResourcePackFiles() {
      return this.dirResourcepacks.isDirectory() ? Arrays.asList(this.dirResourcepacks.listFiles(resourcePackFilter)) : Collections.emptyList();
   }

   public void updateRepositoryEntriesAll() {
      ArrayList var4 = Lists.newArrayList();

      for (File var6 : this.getResourcePackFiles()) {
         ResourcePackRepository.Entry var7 = new ResourcePackRepository.Entry(var6);
         if (!this.repositoryEntriesAll.contains(var7)) {
            try {
               var7.updateResourcePack();
               var4.add(var7);
            } catch (Exception var9) {
               var4.remove(var7);
            }
         } else {
            int var8 = this.repositoryEntriesAll.indexOf(var7);
            if (var8 > -1 && var8 < this.repositoryEntriesAll.size()) {
               var4.add(this.repositoryEntriesAll.get(var8));
            }
         }
      }

      this.repositoryEntriesAll.removeAll(var4);

      for (ResourcePackRepository.Entry var11 : this.repositoryEntriesAll) {
         var11.closeResourcePack();
      }

      this.repositoryEntriesAll = var4;
   }

   public List<ResourcePackRepository.Entry> getRepositoryEntriesAll() {
      return ImmutableList.copyOf(this.repositoryEntriesAll);
   }

   public List<ResourcePackRepository.Entry> getRepositoryEntries() {
      return ImmutableList.copyOf(this.repositoryEntries);
   }

   public void setRepositories(List<ResourcePackRepository.Entry> var1) {
      this.repositoryEntries.clear();
      this.repositoryEntries.addAll(var1);
   }

   public File getDirResourcepacks() {
      return this.dirResourcepacks;
   }

   public ListenableFuture<Object> downloadResourcePack(String var1, String var2) {
      ResourcePackEvent var6 = new ResourcePackEvent(var2);
      Astolfo.INSTANCE.getEventBus().post(var6);
      if (var6.isCancelled()) {
         return this.downloadingPacks;
      }

      String var7;
      if (var2.matches("^[a-f0-9]{40}$")) {
         var7 = var2;
      } else {
         var7 = "legacy";
      }

      final File var8 = new File(this.dirServerResourcepacks, var7);
      this.lock.lock();

      try {
         this.clearResourcePack();
         if (var8.exists() && var2.length() == 40) {
            try {
               String var9 = Hashing.sha1().hashBytes(Files.toByteArray(var8)).toString();
               if (var9.equals(var2)) {
                  return this.setResourcePackInstance(var8);
               }

               logger.warn("File " + var8 + " had wrong hash (expected " + var2 + ", found " + var9 + "). Deleting it.");
               FileUtils.deleteQuietly(var8);
            } catch (IOException var18) {
               logger.warn("File " + var8 + " couldn't be hashed. Deleting it.", var18);
               FileUtils.deleteQuietly(var8);
            }
         }

         this.deleteOldServerResourcesPacks();
         final GuiScreenWorking var20 = new GuiScreenWorking();
         Map var10 = Minecraft.getSessionInfo();
         final Minecraft var11 = Minecraft.getMinecraft();
         Futures.getUnchecked(var11.addScheduledTask(new Runnable() {
            @Override
            public void run() {
               var11.displayGuiScreen(var20);
            }
         }));
         final SettableFuture var12 = SettableFuture.create();
         this.downloadingPacks = HttpUtil.downloadResourcePack(var8, var1, var10, 52428800, var20, var11.getProxy());
         Futures.addCallback(this.downloadingPacks, new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object var1) {
               ResourcePackRepository.this.setResourcePackInstance(var8);
               var12.set(null);
            }

            @Override
            public void onFailure(Throwable var1) {
               var12.setException(var1);
            }
         });
         return this.downloadingPacks;
      } finally {
         this.lock.unlock();
      }
   }

   private void deleteOldServerResourcesPacks() {
      ArrayList var4 = Lists.newArrayList(FileUtils.listFiles(this.dirServerResourcepacks, TrueFileFilter.TRUE, (IOFileFilter)null));
      Collections.sort(var4, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
      int var5 = 0;

      for (Object var7$raw : var4) {
         File var7 = (File)var7$raw;
         if (var5++ >= 10) {
            logger.info("Deleting old server resource pack " + var7.getName());
            FileUtils.deleteQuietly(var7);
         }
      }
   }

   public ListenableFuture<Object> setResourcePackInstance(File var1) {
      this.resourcePackInstance = new FileResourcePack(var1);
      return Minecraft.getMinecraft().scheduleResourcesRefresh();
   }

   public IResourcePack getResourcePackInstance() {
      return this.resourcePackInstance;
   }

   public void clearResourcePack() {
      this.lock.lock();

      try {
         if (this.downloadingPacks != null) {
            this.downloadingPacks.cancel(true);
         }

         this.downloadingPacks = null;
         if (this.resourcePackInstance != null) {
            this.resourcePackInstance = null;
            Minecraft.getMinecraft().scheduleResourcesRefresh();
         }
      } finally {
         this.lock.unlock();
      }
   }

   public class Entry {
      private final File resourcePackFile;
      private IResourcePack reResourcePack;
      private PackMetadataSection rePackMetadataSection;
      private BufferedImage texturePackIcon;
      private ResourceLocation locationTexturePackIcon;
      private static final long b = 70191259786567L;

      public Entry(File var2) {
         this.resourcePackFile = var2;
      }

      public void updateResourcePack() throws IOException {
         this.reResourcePack = this.resourcePackFile.isDirectory()
            ? new FolderResourcePack(this.resourcePackFile)
            : new FileResourcePack(this.resourcePackFile);
         this.rePackMetadataSection = this.reResourcePack.getPackMetadata(ResourcePackRepository.this.rprMetadataSerializer, "pack");

         try {
            this.texturePackIcon = this.reResourcePack.getPackImage();
         } catch (IOException var5) {
         }

         if (this.texturePackIcon == null) {
            this.texturePackIcon = ResourcePackRepository.this.rprDefaultResourcePack.getPackImage();
         }

         this.closeResourcePack();
      }

      public void bindTexturePackIcon(TextureManager var1) {
         if (this.locationTexturePackIcon == null) {
            this.locationTexturePackIcon = var1.getDynamicTextureLocation("texturepackicon", new DynamicTexture(this.texturePackIcon));
         }

         var1.bindTexture(this.locationTexturePackIcon);
      }

      public void closeResourcePack() {
         if (this.reResourcePack instanceof Closeable) {
            IOUtils.closeQuietly((Closeable)this.reResourcePack);
         }
      }

      public IResourcePack getResourcePack() {
         return this.reResourcePack;
      }

      public String getResourcePackName() {
         return this.reResourcePack.getPackName();
      }

      public String getTexturePackDescription() {
         return this.rePackMetadataSection == null
            ? EnumChatFormatting.RED + "Invalid pack.mcmeta (or missing 'pack' section)"
            : this.rePackMetadataSection.getPackDescription().getFormattedText();
      }

      public int func_183027_f() {
         return this.rePackMetadataSection.getPackFormat();
      }

      @Override
      public boolean equals(Object var1) {
         return this == var1 ? true : (var1 instanceof ResourcePackRepository.Entry ? this.toString().equals(var1.toString()) : false);
      }

      @Override
      public int hashCode() {
         return this.toString().hashCode();
      }

      @Override
      public String toString() {
         return String.format(
            "%s:%s:%d", this.resourcePackFile.getName(), this.resourcePackFile.isDirectory() ? "folder" : "zip", this.resourcePackFile.lastModified()
         );
      }
   }
}
