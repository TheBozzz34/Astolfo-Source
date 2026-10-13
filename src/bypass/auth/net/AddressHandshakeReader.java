package bypass.auth.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public final class AddressHandshakeReader {
   private final ByteBuffer w = ByteBuffer.allocateDirect(4);
   private static final long a = 65707301201899L;

   public HandshakeAddresses T(SocketChannel var1) throws IOException {
      boolean var6 = var1.isBlocking();
      var1.configureBlocking(true);
      var1.read(this.w);
      ((Buffer)this.w).flip();
      int var7 = this.w.getInt();
      ((Buffer)this.w).clear();
      ByteBuffer var8 = ByteBuffer.allocate(var7);
      var1.read(var8);
      ((Buffer)var8).flip();
      byte[] var9 = new byte[var7];
      var8.get(var9);
      var1.configureBlocking(var6);
      String var10 = new String(var9, StandardCharsets.UTF_8);
      String var11 = ((InetSocketAddress)var1.getRemoteAddress()).getAddress().getHostAddress();
      return new HandshakeAddresses(var10, var11);
   }
}
