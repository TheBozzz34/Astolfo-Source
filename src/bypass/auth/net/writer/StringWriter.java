package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class StringWriter implements PacketWriter<String> {
   private static final long a = 133138363018977L;

   public void write(String var1, PacketBuffer var2) {
      byte[] var7 = var1.getBytes(StandardCharsets.UTF_8);
      ByteBuffer var8 = ByteBuffer.allocate(var7.length + 4);
      var8.putInt(var7.length);
      var8.put(var7);
      var2.n(var8);
      PacketWriter.super.write(var1, var2);
   }
}
