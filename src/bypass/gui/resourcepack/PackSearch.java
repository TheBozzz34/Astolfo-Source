package bypass.gui.resourcepack;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryFound;

public class PackSearch {
   public static final Comparator<ResourcePackListEntry> b = new PackNameAscending();
   public static final Comparator<ResourcePackListEntry> A = new PackNameDescending();
   private final List<ResourcePackListEntry> M;
   private final List<ResourcePackListEntry> J;
   private Comparator<ResourcePackListEntry> F;
   private Pattern H;
   private static final long a = 33754350313956L;

   private static String T(ResourcePackListEntry resourcePackListEntry) {
      if (resourcePackListEntry instanceof CustomPackEntry) {
         return ((CustomPackEntry)resourcePackListEntry).func_148312_b();
      } else {
         return resourcePackListEntry instanceof ResourcePackListEntryFound
            ? ((ResourcePackListEntryFound)resourcePackListEntry).func_148318_i().getResourcePackName()
            : "<INVALID>";
      }
   }

   private static String c(ResourcePackListEntry resourcePackListEntry, boolean bl) {
      String string = !bl ? "a" : "z";
      String string2 = !bl ? "b" : "z";
      String string3 = !bl ? "z" : "a";
      if (resourcePackListEntry instanceof FolderPackEntry) {
         FolderPackEntry folderPackEntry = (FolderPackEntry)resourcePackListEntry;
         return folderPackEntry.Y ? string + folderPackEntry.B : string2 + folderPackEntry.B;
      } else if (resourcePackListEntry instanceof CustomPackEntry) {
         return string3 + ((CustomPackEntry)resourcePackListEntry).func_148312_b();
      } else {
         return resourcePackListEntry instanceof ResourcePackListEntryFound
            ? string3 + ((ResourcePackListEntryFound)resourcePackListEntry).func_148318_i().getResourcePackName()
            : string3 + "<INVALID>";
      }
   }

   private static String F(ResourcePackListEntry resourcePackListEntry) {
      if (resourcePackListEntry instanceof CustomPackEntry) {
         return ((CustomPackEntry)resourcePackListEntry).func_148311_a();
      } else {
         return resourcePackListEntry instanceof ResourcePackListEntryFound
            ? ((ResourcePackListEntryFound)resourcePackListEntry).func_148318_i().getTexturePackDescription()
            : "<INVALID>";
      }
   }

   public PackSearch(List<ResourcePackListEntry> list, List<ResourcePackListEntry> list2) {
      this.M = list;
      this.J = list2;
      this.f();
   }

   public void E(Comparator<ResourcePackListEntry> comparator) {
      this.F = comparator;
      this.f();
   }

   public void R(String string) {
      if (string != null && !string.isEmpty()) {
         this.H = Pattern.compile("\\Q" + string.replace("*", "\\E.*\\Q") + "\\E", 2);
      } else {
         this.H = null;
      }

      this.f();
   }

   public void f() {
      this.J.clear();

      for (ResourcePackListEntry resourcePackListEntry : this.M) {
         if (this.S(T(resourcePackListEntry)) || this.S(F(resourcePackListEntry))) {
            this.J.add(resourcePackListEntry);
         }
      }

      if (this.F != null) {
         Collections.sort(this.J, this.F);
      }
   }

   private boolean S(String string) {
      return this.H == null || this.H.matcher(string.toLowerCase(Locale.ENGLISH)).find();
   }

   public static String A(ResourcePackListEntry resourcePackListEntry, boolean bl) {
      return c(resourcePackListEntry, bl);
   }
}
