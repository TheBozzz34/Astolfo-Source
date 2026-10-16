package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public interface ChannelOpener {
   SocketChannel openChannelWith(Consumer<SocketChannel> consumer) throws IOException;

   SocketChannel openChannel() throws IOException;
}
