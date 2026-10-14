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

   public void g(SocketChannel socketChannel, SocketAddress socketAddress) throws IOException {
      if (socketChannel.isConnected()) {
         throw new AlreadyConnectedException();
      }

      boolean bl = socketChannel.isBlocking();
      socketChannel.configureBlocking(true);
      socketChannel.connect(socketAddress);
      this.p(socketChannel);
      socketChannel.configureBlocking(bl);
   }

   private void p(SocketChannel socketChannel) throws IOException {
      String string = ((InetSocketAddress)socketChannel.getLocalAddress()).getAddress().getHostAddress();
      byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
      int i = bytes.length;
      this.c.putInt(i);
      ((Buffer)this.c).flip();
      socketChannel.write(this.c);
      ((Buffer)this.c).clear();
      ByteBuffer byteBuffer = ByteBuffer.allocate(i);
      byteBuffer.put(bytes);
      ((Buffer)byteBuffer).flip();
      socketChannel.write(byteBuffer);
   }
}
