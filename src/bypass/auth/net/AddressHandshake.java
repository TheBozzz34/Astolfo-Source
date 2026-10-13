package bypass.auth.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public final class AddressHandshake {
   private final ByteBuffer c = ByteBuffer.allocateDirect(4);
   private static final long a = 122727011848368L;

   public void g(SocketChannel var1, SocketAddress var2) throws IOException {
      if (var1.isConnected()) {
         throw new AlreadyConnectedException();
      }

      boolean var6 = var1.isBlocking();
      var1.configureBlocking(true);
      var1.connect(var2);
      this.p(var1);
      var1.configureBlocking(var6);
   }

   private void p(SocketChannel var1) throws IOException {
      String var2 = ((InetSocketAddress)var1.getLocalAddress()).getAddress().getHostAddress();
      byte[] var3 = var2.getBytes(StandardCharsets.UTF_8);
      int var4 = var3.length;
      this.c.putInt(var4);
      ((Buffer)this.c).flip();
      var1.write(this.c);
      ((Buffer)this.c).clear();
      ByteBuffer var5 = ByteBuffer.allocate(var4);
      var5.put(var3);
      ((Buffer)var5).flip();
      var1.write(var5);
   }
}
