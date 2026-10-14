package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class AbstractConnectionHandler implements ConnectionHandler {
   private final SelectorRegistrar b;
   private final NioConnection N;

   public AbstractConnectionHandler(SelectorRegistrar selectorRegistrar, NioConnection nioConnection) {
      this.b = selectorRegistrar;
      this.N = nioConnection;
   }

   @Override
   public void c(SocketChannel socketChannel) throws IOException {
      this.b.D(this.N, socketChannel);
   }
}
