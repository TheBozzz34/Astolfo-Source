package bypass.auth.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public final class HandshakeConnector implements Connector {
   private static final ConnectionOptions options = ConnectionOptions.ofSize(32);
   private final AddressHandshake addressHandshake = new AddressHandshake();

   public static ConnectorFactory createFactory() {
      return new ConnectorFactoryBuilder().build();
   }

   @Override
   public void connect(Connection connection, ChannelOpener channelOpener, SocketAddress socketAddress) throws IOException {
      SocketChannel socketChannel = channelOpener.openChannel();
      this.addressHandshake.connect(socketChannel, socketAddress);
      connection.open(socketChannel);
   }
}
