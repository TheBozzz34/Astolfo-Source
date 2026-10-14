package bypass.auth.handler;

import bypass.ClientAccess;
import bypass.auth.net.PacketOutput;
import bypass.auth.packet.PacketId;

public abstract class PacketHandler<T> implements ClientAccess {
   private final PacketId packetId;

   public PacketHandler(PacketId packetId) {
      this.packetId = packetId;
   }

   public abstract void handle(T object, PacketOutput packetOutput);

   public PacketId getPacketId() {
      return this.packetId;
   }
}
