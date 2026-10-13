package bypass.auth.net;

import java.io.IOException;
import java.net.SocketAddress;

public interface Connector {
   void K(Connection var1, ChannelOpener var2, SocketAddress var3) throws IOException;
}
