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

   public static ResourcePackRepository.Entry b(ResourcePackRepository var0, File var1) {
      try {
         if (S == null) {
            S = ResourcePackRepository.Entry.class.getDeclaredConstructor(ResourcePackRepository.class, File.class);
            S.setAccessible(true);
         }

         return S.newInstance(var0, var1);
      } catch (Throwable var6) {
         var6.printStackTrace();
         return null;
      }
   }

   public FolderResourcePackRepository(File var1, File var2, IResourcePack var3, IMetadataSerializer var4, GameSettings var5, List<String> var6) {
      super(var1, var2, var3, var4, var5);
      this.updateRepositoryEntriesAll();

      for (String var11 : var6) {
         for (ResourcePackRepository.Entry var13 : this.W) {
            if (var13.getResourcePackName().equals(var11)) {
               this.s.add(var13);
            }
         }
      }
   }

   private final List<File> a(File var1) {
      if (!var1.isDirectory()) {
         return Collections.emptyList();
      }

      ArrayList var5 = Lists.newArrayList();

      for (File var9 : var1.listFiles()) {
         if (var9.isDirectory() && !new File(var9, "pack.mcmeta").isFile()) {
            var5.addAll(this.a(var9));
         } else {
            var5.add(var9);
         }
      }

      return var5;
   }

   @Override
   public void updateRepositoryEntriesAll() {
      if (this.R) {
         ArrayList var4 = Lists.newArrayList();

         for (File var6 : this.a(this.getDirResourcepacks())) {
            ResourcePackRepository.Entry var7 = b(this, var6);
            if (!this.W.contains(var7)) {
               try {
                  var7.updateResourcePack();
                  var4.add(var7);
               } catch (Exception var9) {
                  var4.remove(var7);
               }
            } else {
               int var8 = this.W.indexOf(var7);
               if (var8 > -1 && var8 < this.W.size()) {
                  var4.add(this.W.get(var8));
               }
            }
         }

         this.W.removeAll(var4);

         for (ResourcePackRepository.Entry var11 : this.W) {
            var11.closeResourcePack();
         }

         this.W = var4;
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
   public void setRepositories(List<ResourcePackRepository.Entry> var1) {
      this.s.clear();
      this.s.addAll(var1);
   }
}
