package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public abstract class AbstractNioConnection implements NioConnection {
   public Selector K;

   @Override
   public void g(SocketChannel socketChannel) throws IOException {
      this.K = Selector.open();
   }

   @Override
   public void p(SocketChannel socketChannel, int i) throws ClosedChannelException {
      socketChannel.register(this.K, i);
   }
}
