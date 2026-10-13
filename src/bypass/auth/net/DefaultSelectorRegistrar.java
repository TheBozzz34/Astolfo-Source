package bypass.auth.net;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

public final class DefaultSelectorRegistrar implements SelectorRegistrar {
   @Override
   public void D(NioConnection var1, SocketChannel var2) throws ClosedChannelException {
      var1.p(var2, 1);
   }
}
