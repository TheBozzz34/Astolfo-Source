package bypass.gui.resourcepack;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryFound;

public class PackSearch {
   public static final Comparator<ResourcePackListEntry> NAME_ASCENDING = new PackNameAscending();
   public static final Comparator<ResourcePackListEntry> NAME_DESCENDING = new PackNameDescending();
   private final List<ResourcePackListEntry> allEntries;
   private final List<ResourcePackListEntry> filteredEntries;
   private Comparator<ResourcePackListEntry> comparator;
   private Pattern searchPattern;

   private static String getPackName(ResourcePackListEntry resourcePackListEntry) {
      if (resourcePackListEntry instanceof CustomPackEntry) {
         return ((CustomPackEntry)resourcePackListEntry).func_148312_b();
      } else {
         return resourcePackListEntry instanceof ResourcePackListEntryFound
            ? ((ResourcePackListEntryFound)resourcePackListEntry).func_148318_i().getResourcePackName()
            : "<INVALID>";
      }
   }

   private static String computeSortKey(ResourcePackListEntry resourcePackListEntry, boolean bl) {
      String string = !bl ? "a" : "z";
      String string2 = !bl ? "b" : "z";
      String string3 = !bl ? "z" : "a";
      if (resourcePackListEntry instanceof FolderPackEntry) {
         FolderPackEntry folderPackEntry = (FolderPackEntry)resourcePackListEntry;
         return folderPackEntry.isBackEntry ? string + folderPackEntry.displayName : string2 + folderPackEntry.displayName;
      } else if (resourcePackListEntry instanceof CustomPackEntry) {
         return string3 + ((CustomPackEntry)resourcePackListEntry).func_148312_b();
      } else {
         return resourcePackListEntry instanceof ResourcePackListEntryFound
            ? string3 + ((ResourcePackListEntryFound)resourcePackListEntry).func_148318_i().getResourcePackName()
            : string3 + "<INVALID>";
      }
   }

   private static String getPackDescription(ResourcePackListEntry resourcePackListEntry) {
      if (resourcePackListEntry instanceof CustomPackEntry) {
         return ((CustomPackEntry)resourcePackListEntry).func_148311_a();
      } else {
         return resourcePackListEntry instanceof ResourcePackListEntryFound
            ? ((ResourcePackListEntryFound)resourcePackListEntry).func_148318_i().getTexturePackDescription()
            : "<INVALID>";
      }
   }

   public PackSearch(List<ResourcePackListEntry> list, List<ResourcePackListEntry> list2) {
      this.allEntries = list;
      this.filteredEntries = list2;
      this.updateFilter();
   }

   public void setComparator(Comparator<ResourcePackListEntry> comparator) {
      this.comparator = comparator;
      this.updateFilter();
   }

   public void setSearchQuery(String string) {
      if (string != null && !string.isEmpty()) {
         this.searchPattern = Pattern.compile("\\Q" + string.replace("*", "\\E.*\\Q") + "\\E", 2);
      } else {
         this.searchPattern = null;
      }

      this.updateFilter();
   }

   public void updateFilter() {
      this.filteredEntries.clear();

      for (ResourcePackListEntry resourcePackListEntry : this.allEntries) {
         if (this.matchesSearch(getPackName(resourcePackListEntry)) || this.matchesSearch(getPackDescription(resourcePackListEntry))) {
            this.filteredEntries.add(resourcePackListEntry);
         }
      }

      if (this.comparator != null) {
         Collections.sort(this.filteredEntries, this.comparator);
      }
   }

   private boolean matchesSearch(String string) {
      return this.searchPattern == null || this.searchPattern.matcher(string.toLowerCase(Locale.ENGLISH)).find();
   }

   public static String getSortKey(ResourcePackListEntry resourcePackListEntry, boolean bl) {
      return computeSortKey(resourcePackListEntry, bl);
   }
}
