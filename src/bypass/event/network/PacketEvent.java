package bypass.event.network;

import bypass.event.CancellableEvent;
import net.minecraft.network.Packet;

public class PacketEvent extends CancellableEvent {
   private PacketDirection direction;
   private Packet packet;

   public PacketEvent(PacketDirection var1, Packet var2) {
      this.direction = var1;
      this.packet = var2;
   }

   public Packet getPacket() {
      return this.packet;
   }

   public void setPacket(Packet var1) {
      this.packet = var1;
   }

   public PacketDirection getDirection() {
      return this.direction;
   }
}
