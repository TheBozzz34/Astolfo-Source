package bypass.auth.net;

import java.util.ArrayDeque;
import java.util.Queue;

public final class OutgoingPacket {
   public final short id;
   public final Queue<PacketChunk> chunks;

   public OutgoingPacket(short s) {
      this.id = s;
      this.chunks = new ArrayDeque<>();
   }
}
