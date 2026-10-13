package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class AbstractConnectionHandler implements ConnectionHandler {
   private final SelectorRegistrar b;
   private final NioConnection N;

   public AbstractConnectionHandler(SelectorRegistrar var1, NioConnection var2) {
      this.b = var1;
      this.N = var2;
   }

   @Override
   public void c(SocketChannel var1) throws IOException {
      this.b.D(this.N, var1);
   }
}
