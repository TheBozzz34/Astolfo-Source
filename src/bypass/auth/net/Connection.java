package bypass.auth.net;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.SocketChannel;

public interface Connection extends Closeable {
   void open(SocketChannel socketChannel) throws IOException;

   @Override
   void close() throws IOException;
}
