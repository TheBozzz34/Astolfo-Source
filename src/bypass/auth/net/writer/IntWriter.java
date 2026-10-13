package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import java.nio.ByteBuffer;

public class IntWriter implements PacketWriter<Integer> {
   private static final long a = 40248358356676L;

   public void write(Integer var1, PacketBuffer var2) {
      ByteBuffer var7 = ByteBuffer.allocate(4);
      var7.putInt(var1);
      var2.n(var7);
   }
}
