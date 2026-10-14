package bypass.gui.hud;

import java.util.List;
import net.minecraft.network.Packet;

public class PacketSnapshot {
   public List<Packet<?>> outgoing;
   public List<Packet<?>> incoming;

   public PacketSnapshot(List<Packet<?>> list, List<Packet<?>> list2) {
      this.outgoing = list;
      this.incoming = list2;
   }
}
