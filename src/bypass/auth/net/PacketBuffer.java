package bypass.auth.net;

import java.nio.ByteBuffer;

public final class PacketBuffer {
   private final OutgoingPacket a;

   public PacketBuffer(OutgoingPacket var1) {
      this.a = var1;
   }

   public void n(ByteBuffer var1) {
      this.a.G.add(new PacketChunk(var1));
   }
}
