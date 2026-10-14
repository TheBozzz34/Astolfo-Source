package bypass.auth.net.reader;

import bypass.auth.net.PacketReader;
import bypass.auth.packet.UserListPacket;
import bypass.auth.packet.UserListType;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class UserListReader implements PacketReader<UserListPacket> {
   private UserListPacket g;
   private static final long a = 41791443513041L;

   @Override
   public void read(ByteBuffer byteBuffer) {
      byte[] bytes = new byte[byteBuffer.getInt()];
      byteBuffer.get(bytes);
      byte[] bytes2 = new byte[byteBuffer.getInt()];
      byteBuffer.get(bytes2);
      int i = byteBuffer.getInt();
      int j = byteBuffer.getInt();
      this.g = new UserListPacket(UserListType.values()[i], new String(bytes, StandardCharsets.UTF_8), new String(bytes2, StandardCharsets.UTF_8), j == 1);
   }

   public UserListPacket getPacket() {
      return this.g;
   }
}
