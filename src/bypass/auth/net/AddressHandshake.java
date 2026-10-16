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
   private final ByteBuffer lengthBuffer = ByteBuffer.allocateDirect(4);

   public void connect(SocketChannel socketChannel, SocketAddress socketAddress) throws IOException {
      if (socketChannel.isConnected()) {
         throw new AlreadyConnectedException();
      }

      boolean bl = socketChannel.isBlocking();
      socketChannel.configureBlocking(true);
      socketChannel.connect(socketAddress);
      this.sendLocalAddress(socketChannel);
      socketChannel.configureBlocking(bl);
   }

   private void sendLocalAddress(SocketChannel socketChannel) throws IOException {
      String string = ((InetSocketAddress)socketChannel.getLocalAddress()).getAddress().getHostAddress();
      byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
      int i = bytes.length;
      this.lengthBuffer.putInt(i);
      ((Buffer)this.lengthBuffer).flip();
      socketChannel.write(this.lengthBuffer);
      ((Buffer)this.lengthBuffer).clear();
      ByteBuffer byteBuffer = ByteBuffer.allocate(i);
      byteBuffer.put(bytes);
      ((Buffer)byteBuffer).flip();
      socketChannel.write(byteBuffer);
   }
}
