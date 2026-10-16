package bypass.gui.resourcepack;

import java.util.Comparator;
import net.minecraft.client.resources.ResourcePackListEntry;

public final class PackNameDescending implements Comparator<ResourcePackListEntry> {
   public int compare(ResourcePackListEntry resourcePackListEntry, ResourcePackListEntry resourcePackListEntry2) {
      return -String.CASE_INSENSITIVE_ORDER.compare(PackSearch.getSortKey(resourcePackListEntry, true), PackSearch.getSortKey(resourcePackListEntry2, true));
   }
}
