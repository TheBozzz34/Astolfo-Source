package bypass.auth.handler;

import bypass.ClientAccess;
import bypass.auth.net.PacketOutput;
import bypass.auth.packet.PacketId;

public abstract class PacketHandler<T> implements ClientAccess {
   private final PacketId packetId;

   public PacketHandler(PacketId var1) {
      this.packetId = var1;
   }

   public abstract void handle(T var1, PacketOutput var2);

   public PacketId getPacketId() {
      return this.packetId;
   }
}
