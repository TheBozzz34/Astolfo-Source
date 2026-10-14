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
      @Override
      public boolean accept(File file) {
         boolean bl = file.isFile() && file.getName().endsWith(".zip");
         boolean bl2 = file.isDirectory() && new File(file, "pack.mcmeta").isFile();
         return bl || bl2;
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

   public ResourcePackRepository(
      File dirResourcepacksIn,
      File dirServerResourcepacksIn,
      IResourcePack rprDefaultResourcePackIn,
      IMetadataSerializer rprMetadataSerializerIn,
      GameSettings settings
   ) {
      this.dirResourcepacks = dirResourcepacksIn;
      this.dirServerResourcepacks = dirServerResourcepacksIn;
      this.rprDefaultResourcePack = rprDefaultResourcePackIn;
      this.rprMetadataSerializer = rprMetadataSerializerIn;
      this.fixDirResourcepacks();
      this.updateRepositoryEntriesAll();
      Iterator iterator2 = settings.resourcePacks.iterator();

      while (iterator2.hasNext()) {
         String string = (String)iterator2.next();

         for (ResourcePackRepository.Entry entry : this.repositoryEntriesAll) {
            if (entry.getResourcePackName().equals(string)) {
               if (entry.func_183027_f() == 1 || settings.incompatibleResourcePacks.contains(entry.getResourcePackName())) {
                  this.repositoryEntries.add(entry);
                  break;
               }

               iterator2.remove();
               logger.warn("Removed selected resource pack {} because it's no longer compatible", new Object[]{entry.getResourcePackName()});
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
      ArrayList arrayList = Lists.newArrayList();

      for (File file : this.getResourcePackFiles()) {
         ResourcePackRepository.Entry entry = new ResourcePackRepository.Entry(file);
         if (!this.repositoryEntriesAll.contains(entry)) {
            try {
               entry.updateResourcePack();
               arrayList.add(entry);
            } catch (Exception exception) {
               arrayList.remove(entry);
            }
         } else {
            int i = this.repositoryEntriesAll.indexOf(entry);
            if (i > -1 && i < this.repositoryEntriesAll.size()) {
               arrayList.add(this.repositoryEntriesAll.get(i));
            }
         }
      }

      this.repositoryEntriesAll.removeAll(arrayList);

      for (ResourcePackRepository.Entry entry2 : this.repositoryEntriesAll) {
         entry2.closeResourcePack();
      }

      this.repositoryEntriesAll = arrayList;
   }

   public List<ResourcePackRepository.Entry> getRepositoryEntriesAll() {
      return ImmutableList.copyOf(this.repositoryEntriesAll);
   }

   public List<ResourcePackRepository.Entry> getRepositoryEntries() {
      return ImmutableList.copyOf(this.repositoryEntries);
   }

   public void setRepositories(List<ResourcePackRepository.Entry> repositories) {
      this.repositoryEntries.clear();
      this.repositoryEntries.addAll(repositories);
   }

   public File getDirResourcepacks() {
      return this.dirResourcepacks;
   }

   public ListenableFuture<Object> downloadResourcePack(String url, String hash) {
      ResourcePackEvent resourcePackEvent = new ResourcePackEvent(hash);
      Astolfo.INSTANCE.getEventBus().post(resourcePackEvent);
      if (resourcePackEvent.isCancelled()) {
         return this.downloadingPacks;
      }

      String string;
      if (hash.matches("^[a-f0-9]{40}$")) {
         string = hash;
      } else {
         string = "legacy";
      }

      final File file = new File(this.dirServerResourcepacks, string);
      this.lock.lock();

      try {
         this.clearResourcePack();
         if (file.exists() && hash.length() == 40) {
            try {
               String string2 = Hashing.sha1().hashBytes(Files.toByteArray(file)).toString();
               if (string2.equals(hash)) {
                  return this.setResourcePackInstance(file);
               }

               logger.warn("File " + file + " had wrong hash (expected " + hash + ", found " + string2 + "). Deleting it.");
               FileUtils.deleteQuietly(file);
            } catch (IOException ioException) {
               logger.warn("File " + file + " couldn't be hashed. Deleting it.", ioException);
               FileUtils.deleteQuietly(file);
            }
         }

         this.deleteOldServerResourcesPacks();
         final GuiScreenWorking guiScreenWorking = new GuiScreenWorking();
         Map map = Minecraft.getSessionInfo();
         final Minecraft minecraft = Minecraft.getMinecraft();
         Futures.getUnchecked(minecraft.addScheduledTask(new Runnable() {
            @Override
            public void run() {
               minecraft.displayGuiScreen(guiScreenWorking);
            }
         }));
         final SettableFuture settableFuture = SettableFuture.create();
         this.downloadingPacks = HttpUtil.downloadResourcePack(file, url, map, 52428800, guiScreenWorking, minecraft.getProxy());
         Futures.addCallback(this.downloadingPacks, new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object object) {
               ResourcePackRepository.this.setResourcePackInstance(file);
               settableFuture.set(null);
            }

            @Override
            public void onFailure(Throwable throwable) {
               settableFuture.setException(throwable);
            }
         });
         return this.downloadingPacks;
      } finally {
         this.lock.unlock();
      }
   }

   private void deleteOldServerResourcesPacks() {
      ArrayList arrayList = Lists.newArrayList(FileUtils.listFiles(this.dirServerResourcepacks, TrueFileFilter.TRUE, (IOFileFilter)null));
      Collections.sort(arrayList, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
      int i = 0;

      for (Object object : arrayList) {
         File file = (File)object;
         if (i++ >= 10) {
            logger.info("Deleting old server resource pack " + file.getName());
            FileUtils.deleteQuietly(file);
         }
      }
   }

   public ListenableFuture<Object> setResourcePackInstance(File resourceFile) {
      this.resourcePackInstance = new FileResourcePack(resourceFile);
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

      public Entry(File resourcePackFileIn) {
         this.resourcePackFile = resourcePackFileIn;
      }

      public void updateResourcePack() throws IOException {
         this.reResourcePack = this.resourcePackFile.isDirectory()
            ? new FolderResourcePack(this.resourcePackFile)
            : new FileResourcePack(this.resourcePackFile);
         this.rePackMetadataSection = this.reResourcePack.getPackMetadata(ResourcePackRepository.this.rprMetadataSerializer, "pack");

         try {
            this.texturePackIcon = this.reResourcePack.getPackImage();
         } catch (IOException ioException) {
         }

         if (this.texturePackIcon == null) {
            this.texturePackIcon = ResourcePackRepository.this.rprDefaultResourcePack.getPackImage();
         }

         this.closeResourcePack();
      }

      public void bindTexturePackIcon(TextureManager textureManagerIn) {
         if (this.locationTexturePackIcon == null) {
            this.locationTexturePackIcon = textureManagerIn.getDynamicTextureLocation("texturepackicon", new DynamicTexture(this.texturePackIcon));
         }

         textureManagerIn.bindTexture(this.locationTexturePackIcon);
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
      public boolean equals(Object object) {
         return this == object ? true : (object instanceof ResourcePackRepository.Entry ? this.toString().equals(object.toString()) : false);
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
