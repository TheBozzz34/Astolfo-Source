package bypass.auth.net;

import java.nio.ByteBuffer;

public interface PacketReader<T> {
   void read(ByteBuffer var1);

   T getPacket();
}
