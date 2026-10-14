package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import bypass.auth.packet.BansPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BansWriter implements PacketWriter<BansPacket> {
   private static final long a = 72725189486655L;
   private static final long b = 4512057839610494988L;

   public void write(BansPacket bansPacket, PacketBuffer packetBuffer) {
      byte[] bytes = bansPacket.bans.getBytes(StandardCharsets.UTF_8);
      ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + (int)b);
      byteBuffer.putInt(bytes.length);
      byteBuffer.put(bytes);
      byteBuffer.putInt(bansPacket.mode);
      byteBuffer.putInt(bansPacket.type.ordinal());
      packetBuffer.n(byteBuffer);
   }
}
