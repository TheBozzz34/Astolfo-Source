package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import bypass.auth.packet.UserListPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class UserListWriter implements PacketWriter<UserListPacket> {
   private static final long a = 74807690517006L;
   private static final long b = -5161745731316350960L;

   public void write(UserListPacket var1, PacketBuffer var2) {
      byte[] var7 = var1.users.getBytes(StandardCharsets.UTF_8);
      byte[] var8 = var1.s.getBytes(StandardCharsets.UTF_8);
      ByteBuffer var9 = ByteBuffer.allocate(var7.length + var8.length + (int)b);
      var9.putInt(var7.length);
      var9.put(var7);
      var9.putInt(var8.length);
      var9.put(var8);
      var9.putInt(var1.type.ordinal());
      var9.putInt(var1.H ? 1 : 0);
      var2.n(var9);
   }
}
