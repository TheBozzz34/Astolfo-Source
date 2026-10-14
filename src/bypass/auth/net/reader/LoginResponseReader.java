package bypass.auth.net.reader;

import bypass.auth.net.PacketReader;
import bypass.auth.packet.LoginResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public final class LoginResponseReader implements PacketReader<LoginResponse> {
   private LoginResponse D;
   private static final long a = 82877989959479L;

   @Override
   public void read(ByteBuffer byteBuffer) {
      byte[] bytes = new byte[byteBuffer.getInt()];
      byteBuffer.get(bytes);
      boolean bl = byteBuffer.getInt() == 1;
      this.D = new LoginResponse(bl, new String(bytes, StandardCharsets.UTF_8));
   }

   public LoginResponse getPacket() {
      return this.D;
   }
}
