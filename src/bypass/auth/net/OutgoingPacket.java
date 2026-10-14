package bypass.auth.net;

import java.util.ArrayDeque;
import java.util.Queue;

public final class OutgoingPacket {
   public final short Z;
   public final Queue<PacketChunk> G;

   public OutgoingPacket(short s) {
      this.Z = s;
      this.G = new ArrayDeque<>();
   }
}
