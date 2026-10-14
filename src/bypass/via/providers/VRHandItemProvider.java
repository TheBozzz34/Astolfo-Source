package bypass.via.providers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VRHandItemProvider extends HandItemProvider {
   public Item Z = null;
   public final Map<UUID, Item> j = new ConcurrentHashMap<>();
   private static final long a = 102387469170387L;

   @Override
   public Item getHandItem(UserConnection userConnection) {
      if (userConnection.isClientSide()) {
         return this.b();
      }

      Item item;
      return (item = this.j.get(userConnection.getProtocolInfo().getUuid())) != null ? new DataItem(item) : super.getHandItem(userConnection);
   }

   private Item b() {
      return this.Z == null ? new DataItem(0, (byte)0, (short)0, null) : new DataItem(this.Z);
   }
}
