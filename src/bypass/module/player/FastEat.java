package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.module.Category;
import bypass.module.Module;

public class FastEat extends Module {
   private int unusedIntB;
   private int unusedIntA;
   private boolean unusedFlagA;
   private boolean unusedFlagB;
   @EventHandler(value = PacketEvent.class, priority = EventPriority.HIGH)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
      }
   };

   public FastEat() {
      super("FastEat", Category.PLAYER);
   }
}
