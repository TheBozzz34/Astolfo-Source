package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import bypass.auth.packet.BansPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BansWriter implements PacketWriter<BansPacket> {
   public void write(BansPacket bansPacket, PacketBuffer packetBuffer) {
      byte[] bytes = bansPacket.bans.getBytes(StandardCharsets.UTF_8);
      ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + 12);
      byteBuffer.putInt(bytes.length);
      byteBuffer.put(bytes);
      byteBuffer.putInt(bansPacket.mode);
      byteBuffer.putInt(bansPacket.type.ordinal());
      packetBuffer.n(byteBuffer);
   }
}
