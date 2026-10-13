package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public final class DefaultConnectionHandler extends AbstractConnectionHandler {
   public DefaultConnectionHandler(SelectorRegistrar var1, NioConnection var2) {
      super(var1, var2);
   }

   @Override
   public void c(SocketChannel var1) throws IOException {
      var1.configureBlocking(false);
      super.c(var1);
   }
}
