package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public enum SocketChannelOpener implements ChannelOpener {
   INSTANCE;

   @Override
   public SocketChannel openChannelWith(Consumer<SocketChannel> consumer) throws IOException {
      SocketChannel socketChannel = SocketChannel.open();
      consumer.accept(socketChannel);
      return socketChannel;
   }

   @Override
   public SocketChannel openChannel() throws IOException {
      return SocketChannel.open();
   }
}
