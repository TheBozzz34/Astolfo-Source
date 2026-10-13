package bypass.gui.hud;

import java.util.List;
import net.minecraft.network.Packet;

public class PacketSnapshot {
   public List<Packet<?>> outgoing;
   public List<Packet<?>> incoming;

   public PacketSnapshot(List<Packet<?>> var1, List<Packet<?>> var2) {
      this.outgoing = var1;
      this.incoming = var2;
   }
}
