package bypass.auth.net;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

public final class DefaultSelectorRegistrar implements SelectorRegistrar {
   @Override
   public void register(NioConnection nioConnection, SocketChannel socketChannel) throws ClosedChannelException {
      nioConnection.registerChannel(socketChannel, 1);
   }
}
