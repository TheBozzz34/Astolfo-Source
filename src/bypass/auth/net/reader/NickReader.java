package bypass.auth.net.reader;

import bypass.auth.net.PacketReader;
import bypass.auth.packet.NickPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class NickReader implements PacketReader<NickPacket> {
   private NickPacket r;
   private static final long a = 75223491193498L;

   @Override
   public void read(ByteBuffer var1) {
      byte[] var6 = new byte[var1.getInt()];
      var1.get(var6);
      byte[] var7 = new byte[var1.getInt()];
      var1.get(var7);
      this.r = new NickPacket(new String(var6, StandardCharsets.UTF_8), new String(var7, StandardCharsets.UTF_8));
   }

   public NickPacket getPacket() {
      return this.r;
   }
}
