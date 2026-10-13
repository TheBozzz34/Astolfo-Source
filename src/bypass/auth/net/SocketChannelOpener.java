package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public enum SocketChannelOpener implements ChannelOpener {
   INSTANCE;

   @Override
   public SocketChannel A(Consumer<SocketChannel> var1) throws IOException {
      SocketChannel var2 = SocketChannel.open();
      var1.accept(var2);
      return var2;
   }

   @Override
   public SocketChannel j() throws IOException {
      return SocketChannel.open();
   }
}
