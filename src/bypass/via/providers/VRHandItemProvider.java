package bypass.via.providers;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VRHandItemProvider extends HandItemProvider {
   public Item clientHandItem = null;
   public final Map<UUID, Item> handItems = new ConcurrentHashMap<>();

   @Override
   public Item getHandItem(UserConnection userConnection) {
      if (userConnection.isClientSide()) {
         return this.getClientHandItem();
      }

      Item item;
      return (item = this.handItems.get(userConnection.getProtocolInfo().getUuid())) != null ? new DataItem(item) : super.getHandItem(userConnection);
   }

   private Item getClientHandItem() {
      return this.clientHandItem == null ? new DataItem(0, (byte)0, (short)0, null) : new DataItem(this.clientHandItem);
   }
}
