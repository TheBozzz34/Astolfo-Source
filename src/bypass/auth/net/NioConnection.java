package bypass.auth.net;

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;

public interface NioConnection extends Closeable {
   void start(SocketChannel socketChannel) throws IOException;

   @Override
   void close() throws IOException;

   void registerChannel(SocketChannel socketChannel, int i) throws ClosedChannelException;
}
