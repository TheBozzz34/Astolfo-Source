package bypass.auth.net.reader;

import bypass.auth.net.PacketReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class StringReader implements PacketReader<String> {
   private String U;

   @Override
   public void read(ByteBuffer byteBuffer) {
      byte[] bytes = new byte[byteBuffer.getInt()];
      byteBuffer.get(bytes);
      this.U = new String(bytes, StandardCharsets.UTF_8);
   }

   public String getPacket() {
      return this.U;
   }
}
