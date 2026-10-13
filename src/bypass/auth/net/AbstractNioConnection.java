package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public abstract class AbstractNioConnection implements NioConnection {
   public Selector K;

   @Override
   public void g(SocketChannel var1) throws IOException {
      this.K = Selector.open();
   }

   @Override
   public void p(SocketChannel var1, int var2) throws ClosedChannelException {
      var1.register(this.K, var2);
   }
}
