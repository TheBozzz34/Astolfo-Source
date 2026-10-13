package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface ConnectionHandler {
   void c(SocketChannel var1) throws IOException;
}
