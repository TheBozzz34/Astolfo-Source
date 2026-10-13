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
   public void read(ByteBuffer var1) {
      byte[] var5 = new byte[var1.getInt()];
      var1.get(var5);
      byte[] var6 = new byte[var1.getInt()];
      var1.get(var6);
      int var7 = var1.getInt();
      int var8 = var1.getInt();
      this.g = new UserListPacket(UserListType.values()[var7], new String(var5, StandardCharsets.UTF_8), new String(var6, StandardCharsets.UTF_8), var8 == 1);
   }

   public UserListPacket getPacket() {
      return this.g;
   }
}
