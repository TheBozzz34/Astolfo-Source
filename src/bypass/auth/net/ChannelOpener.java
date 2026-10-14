package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public interface ChannelOpener {
   SocketChannel A(Consumer<SocketChannel> consumer) throws IOException;

   SocketChannel j() throws IOException;
}
