package bypass.gui.resourcepack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.settings.GameSettings;

public class FolderResourcePackRepository extends ResourcePackRepository {
   private static Constructor<ResourcePackRepository.Entry> entryConstructor;
   private List<ResourcePackRepository.Entry> allPackEntries = Lists.newArrayList();
   private List<ResourcePackRepository.Entry> activePackEntries = Lists.newArrayList();
   private boolean rescanEnabled = true;

   public static ResourcePackRepository.Entry createEntry(ResourcePackRepository resourcePackRepository, File file) {
      try {
         if (entryConstructor == null) {
            entryConstructor = ResourcePackRepository.Entry.class.getDeclaredConstructor(ResourcePackRepository.class, File.class);
            entryConstructor.setAccessible(true);
         }

         return entryConstructor.newInstance(resourcePackRepository, file);
      } catch (Throwable throwable) {
         throwable.printStackTrace();
         return null;
      }
   }

   public FolderResourcePackRepository(
      File file, File file2, IResourcePack iResourcePack, IMetadataSerializer iMetadataSerializer, GameSettings gameSettings, List<String> list
   ) {
      super(file, file2, iResourcePack, iMetadataSerializer, gameSettings);
      this.updateRepositoryEntriesAll();

      for (String string : list) {
         for (ResourcePackRepository.Entry entry : this.allPackEntries) {
            if (entry.getResourcePackName().equals(string)) {
               this.activePackEntries.add(entry);
            }
         }
      }
   }

   private final List<File> listPackFilesRecursive(File file) {
      if (!file.isDirectory()) {
         return Collections.emptyList();
      }

      ArrayList arrayList = Lists.newArrayList();

      for (File file2 : file.listFiles()) {
         if (file2.isDirectory() && !new File(file2, "pack.mcmeta").isFile()) {
            arrayList.addAll(this.listPackFilesRecursive(file2));
         } else {
            arrayList.add(file2);
         }
      }

      return arrayList;
   }

   @Override
   public void updateRepositoryEntriesAll() {
      if (this.rescanEnabled) {
         ArrayList arrayList = Lists.newArrayList();

         for (File file : this.listPackFilesRecursive(this.getDirResourcepacks())) {
            ResourcePackRepository.Entry entry = createEntry(this, file);
            if (!this.allPackEntries.contains(entry)) {
               try {
                  entry.updateResourcePack();
                  arrayList.add(entry);
               } catch (Exception exception) {
                  arrayList.remove(entry);
               }
            } else {
               int i = this.allPackEntries.indexOf(entry);
               if (i > -1 && i < this.allPackEntries.size()) {
                  arrayList.add(this.allPackEntries.get(i));
               }
            }
         }

         this.allPackEntries.removeAll(arrayList);

         for (ResourcePackRepository.Entry entry2 : this.allPackEntries) {
            entry2.closeResourcePack();
         }

         this.allPackEntries = arrayList;
      }
   }

   @Override
   public List<ResourcePackRepository.Entry> getRepositoryEntriesAll() {
      return ImmutableList.copyOf(this.allPackEntries);
   }

   @Override
   public List<ResourcePackRepository.Entry> getRepositoryEntries() {
      return ImmutableList.copyOf(this.activePackEntries);
   }

   @Override
   public void setRepositories(List<ResourcePackRepository.Entry> repositories) {
      this.activePackEntries.clear();
      this.activePackEntries.addAll(repositories);
   }
}
