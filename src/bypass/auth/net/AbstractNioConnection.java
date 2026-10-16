package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public abstract class AbstractNioConnection implements NioConnection {
   public Selector selector;

   @Override
   public void start(SocketChannel socketChannel) throws IOException {
      this.selector = Selector.open();
   }

   @Override
   public void registerChannel(SocketChannel socketChannel, int i) throws ClosedChannelException {
      socketChannel.register(this.selector, i);
   }
}
