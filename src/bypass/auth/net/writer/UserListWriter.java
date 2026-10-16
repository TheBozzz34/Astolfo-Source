package bypass.auth.net.writer;

import bypass.auth.net.PacketBuffer;
import bypass.auth.net.PacketWriter;
import bypass.auth.packet.UserListPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class UserListWriter implements PacketWriter<UserListPacket> {
   public void write(UserListPacket userListPacket, PacketBuffer packetBuffer) {
      byte[] bytes = userListPacket.users.getBytes(StandardCharsets.UTF_8);
      byte[] bytes2 = userListPacket.extra.getBytes(StandardCharsets.UTF_8);
      ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length + bytes2.length + 16);
      byteBuffer.putInt(bytes.length);
      byteBuffer.put(bytes);
      byteBuffer.putInt(bytes2.length);
      byteBuffer.put(bytes2);
      byteBuffer.putInt(userListPacket.type.ordinal());
      byteBuffer.putInt(userListPacket.sense ? 1 : 0);
      packetBuffer.addChunk(byteBuffer);
   }
}
