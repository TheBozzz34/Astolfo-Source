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
   public void read(ByteBuffer var1) {
      byte[] var5 = new byte[var1.getInt()];
      var1.get(var5);
      int var6 = var1.getInt();
      int var7 = var1.getInt();
      this.B = new BansPacket(BansPacketType.values()[var7], var6, new String(var5, StandardCharsets.UTF_8));
   }

   public BansPacket getPacket() {
      return this.B;
   }
}
