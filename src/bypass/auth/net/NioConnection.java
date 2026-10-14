package bypass.auth.net;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

public interface NioConnection extends Closeable {
   void g(SocketChannel socketChannel) throws IOException;

   @Override
   void close() throws IOException;

   void p(SocketChannel socketChannel, int i) throws ClosedChannelException;
}
