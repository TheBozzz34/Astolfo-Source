package bypass.gui.resourcepack;

import java.util.Comparator;
import net.minecraft.client.resources.ResourcePackListEntry;

public final class PackNameAscending implements Comparator<ResourcePackListEntry> {
   public int compare(ResourcePackListEntry resourcePackListEntry, ResourcePackListEntry resourcePackListEntry2) {
      return String.CASE_INSENSITIVE_ORDER.compare(PackSearch.A(resourcePackListEntry, false), PackSearch.A(resourcePackListEntry2, false));
   }
}
