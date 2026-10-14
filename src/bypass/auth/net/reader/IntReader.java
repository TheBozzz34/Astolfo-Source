package bypass.auth.net.reader;

import bypass.auth.net.PacketReader;
import java.nio.ByteBuffer;

public class IntReader implements PacketReader<Integer> {
   private Integer g;

   @Override
   public void read(ByteBuffer byteBuffer) {
      this.g = byteBuffer.getInt();
   }

   public Integer getPacket() {
      return this.g;
   }
}
