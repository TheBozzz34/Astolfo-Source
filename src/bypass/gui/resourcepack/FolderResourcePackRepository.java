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
   private static Constructor<ResourcePackRepository.Entry> S;
   private List<ResourcePackRepository.Entry> W = Lists.newArrayList();
   private List<ResourcePackRepository.Entry> s = Lists.newArrayList();
   private boolean R = true;
   private static final long a = 112939523825849L;

   public static ResourcePackRepository.Entry b(ResourcePackRepository resourcePackRepository, File file) {
      try {
         if (S == null) {
            S = ResourcePackRepository.Entry.class.getDeclaredConstructor(ResourcePackRepository.class, File.class);
            S.setAccessible(true);
         }

         return S.newInstance(resourcePackRepository, file);
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
         for (ResourcePackRepository.Entry entry : this.W) {
            if (entry.getResourcePackName().equals(string)) {
               this.s.add(entry);
            }
         }
      }
   }

   private final List<File> a(File file) {
      if (!file.isDirectory()) {
         return Collections.emptyList();
      }

      ArrayList arrayList = Lists.newArrayList();

      for (File file2 : file.listFiles()) {
         if (file2.isDirectory() && !new File(file2, "pack.mcmeta").isFile()) {
            arrayList.addAll(this.a(file2));
         } else {
            arrayList.add(file2);
         }
      }

      return arrayList;
   }

   @Override
   public void updateRepositoryEntriesAll() {
      if (this.R) {
         ArrayList arrayList = Lists.newArrayList();

         for (File file : this.a(this.getDirResourcepacks())) {
            ResourcePackRepository.Entry entry = b(this, file);
            if (!this.W.contains(entry)) {
               try {
                  entry.updateResourcePack();
                  arrayList.add(entry);
               } catch (Exception exception) {
                  arrayList.remove(entry);
               }
            } else {
               int i = this.W.indexOf(entry);
               if (i > -1 && i < this.W.size()) {
                  arrayList.add(this.W.get(i));
               }
            }
         }

         this.W.removeAll(arrayList);

         for (ResourcePackRepository.Entry entry2 : this.W) {
            entry2.closeResourcePack();
         }

         this.W = arrayList;
      }
   }

   @Override
   public List<ResourcePackRepository.Entry> getRepositoryEntriesAll() {
      return ImmutableList.copyOf(this.W);
   }

   @Override
   public List<ResourcePackRepository.Entry> getRepositoryEntries() {
      return ImmutableList.copyOf(this.s);
   }

   @Override
   public void setRepositories(List<ResourcePackRepository.Entry> repositories) {
      this.s.clear();
      this.s.addAll(repositories);
   }
}
