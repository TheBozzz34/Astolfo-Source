package bypass.auth.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public final class AddressHandshakeReader {
   private final ByteBuffer w = ByteBuffer.allocateDirect(4);

   public HandshakeAddresses T(SocketChannel socketChannel) throws IOException {
      boolean bl = socketChannel.isBlocking();
      socketChannel.configureBlocking(true);
      socketChannel.read(this.w);
      ((Buffer)this.w).flip();
      int i = this.w.getInt();
      ((Buffer)this.w).clear();
      ByteBuffer byteBuffer = ByteBuffer.allocate(i);
      socketChannel.read(byteBuffer);
      ((Buffer)byteBuffer).flip();
      byte[] bytes = new byte[i];
      byteBuffer.get(bytes);
      socketChannel.configureBlocking(bl);
      String string = new String(bytes, StandardCharsets.UTF_8);
      String string2 = ((InetSocketAddress)socketChannel.getRemoteAddress()).getAddress().getHostAddress();
      return new HandshakeAddresses(string, string2);
   }
}
