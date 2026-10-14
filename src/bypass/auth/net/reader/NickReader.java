package bypass.auth.net.reader;

import bypass.auth.net.PacketReader;
import bypass.auth.packet.NickPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class NickReader implements PacketReader<NickPacket> {
   private NickPacket r;
   private static final long a = 75223491193498L;

   @Override
   public void read(ByteBuffer byteBuffer) {
      byte[] bytes = new byte[byteBuffer.getInt()];
      byteBuffer.get(bytes);
      byte[] bytes2 = new byte[byteBuffer.getInt()];
      byteBuffer.get(bytes2);
      this.r = new NickPacket(new String(bytes, StandardCharsets.UTF_8), new String(bytes2, StandardCharsets.UTF_8));
   }

   public NickPacket getPacket() {
      return this.r;
   }
}
