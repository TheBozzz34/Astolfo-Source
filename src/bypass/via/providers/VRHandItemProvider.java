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
   public Item getHandItem(UserConnection var1) {
      if (var1.isClientSide()) {
         return this.b();
      }

      Item var5;
      return (var5 = this.j.get(var1.getProtocolInfo().getUuid())) != null ? new DataItem(var5) : super.getHandItem(var1);
   }

   private Item b() {
      return this.Z == null ? new DataItem(0, (byte)0, (short)0, null) : new DataItem(this.Z);
   }
}
