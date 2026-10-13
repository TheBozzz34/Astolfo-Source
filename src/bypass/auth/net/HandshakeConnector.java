package bypass.auth.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public final class HandshakeConnector implements Connector {
   private static final ConnectionOptions t = ConnectionOptions.M(32);
   private final AddressHandshake P = new AddressHandshake();

   public static ConnectorFactory l() {
      return new ConnectorFactoryBuilder().Z();
   }

   @Override
   public void K(Connection var1, ChannelOpener var2, SocketAddress var3) throws IOException {
      SocketChannel var4 = var2.j();
      this.P.g(var4, var3);
      var1.open(var4);
   }
}
