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
   public void K(Connection connection, ChannelOpener channelOpener, SocketAddress socketAddress) throws IOException {
      SocketChannel socketChannel = channelOpener.j();
      this.P.g(socketChannel, socketAddress);
      connection.open(socketChannel);
   }
}
