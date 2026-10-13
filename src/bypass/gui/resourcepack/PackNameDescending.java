package bypass.gui.resourcepack;

import java.util.Comparator;
import net.minecraft.client.resources.ResourcePackListEntry;

public final class PackNameDescending implements Comparator<ResourcePackListEntry> {
   public int compare(ResourcePackListEntry var1, ResourcePackListEntry var2) {
      return -String.CASE_INSENSITIVE_ORDER.compare(PackSearch.A(var1, true), PackSearch.A(var2, true));
   }
}
