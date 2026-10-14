package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.module.Category;
import bypass.module.Module;

public final class PacketDebug extends Module {
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
      }
   };
   private static final long c = 7624118149723L;

   public PacketDebug() {
      super("PacketDebug", Category.OTHER);
      this.setHidden(false);
   }
}
