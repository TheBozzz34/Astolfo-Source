package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import java.nio.ByteBuffer;

public class IntWriter implements PacketWriter<Integer> {
   public void write(Integer integer, PacketBuffer packetBuffer) {
      ByteBuffer byteBuffer = ByteBuffer.allocate(4);
      byteBuffer.putInt(integer);
      packetBuffer.n(byteBuffer);
   }
}
