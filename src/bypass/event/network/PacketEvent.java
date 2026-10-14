package bypass.event.network;

import bypass.event.CancellableEvent;
import net.minecraft.network.Packet;

public class PacketEvent extends CancellableEvent {
   private PacketDirection direction;
   private Packet packet;

   public PacketEvent(PacketDirection packetDirection, Packet packet) {
      this.direction = packetDirection;
      this.packet = packet;
   }

   public Packet getPacket() {
      return this.packet;
   }

   public void setPacket(Packet packet) {
      this.packet = packet;
   }

   public PacketDirection getDirection() {
      return this.direction;
   }
}
