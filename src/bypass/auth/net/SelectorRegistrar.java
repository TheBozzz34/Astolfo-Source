package bypass.auth.net;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

public interface SelectorRegistrar {
   void D(NioConnection var1, SocketChannel var2) throws ClosedChannelException;
}
