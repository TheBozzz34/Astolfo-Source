package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import bypass.auth.packet.BansPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BansWriter implements PacketWriter<BansPacket> {
   private static final long a = 72725189486655L;
   private static final long b = 4512057839610494988L;

   public void write(BansPacket var1, PacketBuffer var2) {
      byte[] var7 = var1.bans.getBytes(StandardCharsets.UTF_8);
      ByteBuffer var8 = ByteBuffer.allocate(var7.length + (int)b);
      var8.putInt(var7.length);
      var8.put(var7);
      var8.putInt(var1.mode);
      var8.putInt(var1.type.ordinal());
      var2.n(var8);
   }
}
