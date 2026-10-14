package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import bypass.auth.packet.UserListPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class UserListWriter implements PacketWriter<UserListPacket> {
   private static final long a = 74807690517006L;
   private static final long b = -5161745731316350960L;

   public void write(UserListPacket userListPacket, PacketBuffer packetBuffer) {
      byte[] bytes = userListPacket.users.getBytes(StandardCharsets.UTF_8);
      byte[] bytes2 = userListPacket.s.getBytes(StandardCharsets.UTF_8);
      ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + bytes2.length + (int)b);
      byteBuffer.putInt(bytes.length);
      byteBuffer.put(bytes);
      byteBuffer.putInt(bytes2.length);
      byteBuffer.put(bytes2);
      byteBuffer.putInt(userListPacket.type.ordinal());
      byteBuffer.putInt(userListPacket.H ? 1 : 0);
      packetBuffer.n(byteBuffer);
   }
}
