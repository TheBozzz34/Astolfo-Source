package bypass.auth.net.reader;

import bypass.auth.net.PacketReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class StringReader implements PacketReader<String> {
   private String U;
   private static final long a = 133611954365423L;

   @Override
   public void read(ByteBuffer var1) {
      byte[] var5 = new byte[var1.getInt()];
      var1.get(var5);
      this.U = new String(var5, StandardCharsets.UTF_8);
   }

   public String getPacket() {
      return this.U;
   }
}
