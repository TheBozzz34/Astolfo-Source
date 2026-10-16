package bypass.auth.net;

import java.nio.ByteBuffer;

public final class PacketChunk {
   public final ByteBuffer buffer;

   public PacketChunk(ByteBuffer byteBuffer) {
      this.buffer = byteBuffer;
   }
}
