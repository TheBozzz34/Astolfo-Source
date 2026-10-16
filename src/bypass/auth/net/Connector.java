package bypass.auth.net;

import java.io.IOException;
import java.net.SocketAddress;

public interface Connector {
   void connect(Connection connection, ChannelOpener channelOpener, SocketAddress socketAddress) throws IOException;
}
