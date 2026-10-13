package bypass.auth.net.reader;

import bypass.auth.net.PacketReader;
import bypass.auth.packet.LoginResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class LoginResponseReader implements PacketReader<LoginResponse> {
   private LoginResponse D;
   private static final long a = 82877989959479L;

   @Override
   public void read(ByteBuffer var1) {
      byte[] var6 = new byte[var1.getInt()];
      var1.get(var6);
      boolean var7 = var1.getInt() == 1;
      this.D = new LoginResponse(var7, new String(var6, StandardCharsets.UTF_8));
   }

   public LoginResponse getPacket() {
      return this.D;
   }
}
