package bypass.auth.net;

import java.nio.ByteBuffer;

public final class PacketChunk {
   public final ByteBuffer u;

   public PacketChunk(ByteBuffer byteBuffer) {
      this.u = byteBuffer;
   }
}
