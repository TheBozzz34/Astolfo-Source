package bypass.auth.net;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

public final class DefaultSelectorRegistrar implements SelectorRegistrar {
   @Override
   public void D(NioConnection nioConnection, SocketChannel socketChannel) throws ClosedChannelException {
      nioConnection.p(socketChannel, 1);
   }
}
