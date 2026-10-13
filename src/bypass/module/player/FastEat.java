package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.module.Category;
import bypass.module.Module;

public class FastEat extends Module {
   private int B;
   private int h;
   private boolean H;
   private boolean D;
   @EventHandler(value = PacketEvent.class, priority = EventPriority.HIGH)
   private Listener<PacketEvent> onPacket = var0 -> {
      if (var0.getDirection() == PacketDirection.OUTGOING) {
      }
   };
   private static final long c = 97795217944806L;

   public FastEat() {
      super("FastEat", Category.PLAYER);
   }
}
