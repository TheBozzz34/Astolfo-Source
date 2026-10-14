package bypass.auth.net;

import java.nio.ByteBuffer;

public final class PacketBuffer {
   private final OutgoingPacket a;

   public PacketBuffer(OutgoingPacket outgoingPacket) {
      this.a = outgoingPacket;
   }

   public void n(ByteBuffer byteBuffer) {
      this.a.G.add(new PacketChunk(byteBuffer));
   }
}
