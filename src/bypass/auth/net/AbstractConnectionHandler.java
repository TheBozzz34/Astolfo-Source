package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class AbstractConnectionHandler implements ConnectionHandler {
   private final SelectorRegistrar selectorRegistrar;
   private final NioConnection connection;

   public AbstractConnectionHandler(SelectorRegistrar selectorRegistrar, NioConnection nioConnection) {
      this.selectorRegistrar = selectorRegistrar;
      this.connection = nioConnection;
   }

   @Override
   public void handleConnected(SocketChannel socketChannel) throws IOException {
      this.selectorRegistrar.register(this.connection, socketChannel);
   }
}
