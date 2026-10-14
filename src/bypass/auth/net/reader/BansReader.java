package bypass.auth.net.reader;

import bypass.auth.net.PacketReader;
import bypass.auth.packet.BansPacket;
import bypass.auth.packet.BansPacketType;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BansReader implements PacketReader<BansPacket> {
   private BansPacket B;
   private static final long a = 38919048409795L;

   @Override
   public void read(ByteBuffer byteBuffer) {
      byte[] bytes = new byte[byteBuffer.getInt()];
      byteBuffer.get(bytes);
      int i = byteBuffer.getInt();
      int j = byteBuffer.getInt();
      this.B = new BansPacket(BansPacketType.values()[j], i, new String(bytes, StandardCharsets.UTF_8));
   }

   public BansPacket getPacket() {
      return this.B;
   }
}
