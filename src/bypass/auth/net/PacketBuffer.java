package bypass.auth.net;

import java.nio.ByteBuffer;

public final class PacketBuffer {
   private final OutgoingPacket packet;

   public PacketBuffer(OutgoingPacket outgoingPacket) {
      this.packet = outgoingPacket;
   }

   public void addChunk(ByteBuffer byteBuffer) {
      this.packet.chunks.add(new PacketChunk(byteBuffer));
   }
}
