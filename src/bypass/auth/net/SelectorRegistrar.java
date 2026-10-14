package bypass.auth.net;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

public interface SelectorRegistrar {
   void D(NioConnection nioConnection, SocketChannel socketChannel) throws ClosedChannelException;
}
