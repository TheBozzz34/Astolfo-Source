package bypass.auth.net;

import java.nio.ByteBuffer;

public interface PacketReader<T> {
   void read(ByteBuffer byteBuffer);

   T getPacket();
}
