package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public final class DefaultConnectionHandler extends AbstractConnectionHandler {
   public DefaultConnectionHandler(SelectorRegistrar selectorRegistrar, NioConnection nioConnection) {
      super(selectorRegistrar, nioConnection);
   }

   @Override
   public void handleConnected(SocketChannel socketChannel) throws IOException {
      socketChannel.configureBlocking(false);
      super.handleConnected(socketChannel);
   }
}
