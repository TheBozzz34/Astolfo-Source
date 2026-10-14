package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SocketConnection implements Connection {
   public final PacketConnection C;
   public final ConnectionHandlerFactory K;
   private SocketChannel T;

   public SocketConnection(
      ConnectionListener connectionListener,
      Map<Short, PacketReader<?>> map,
      Map<Short, List<PacketListener<?>>> map2,
      PacketOutput packetOutput,
      ConnectionHandlerFactory connectionHandlerFactory,
      Consumer<IOException> consumer,
      Runnable runnable
   ) {
      this.C = new PacketConnection(connectionListener, map, map2, packetOutput, () -> {
         this.O(consumer);
         runnable.run();
      });
      this.K = connectionHandlerFactory;
   }

   private void O(Consumer<IOException> consumer) {
      try {
         this.K.y(this.C).c(this.T);
      } catch (IOException ioException) {
         consumer.accept(ioException);
      }
   }

   @Override
   public void open(SocketChannel socketChannel) throws IOException {
      this.T = socketChannel;
      this.C.g(socketChannel);
   }

   @Override
   public void close() throws IOException {
      this.C.close();
      this.T.close();
   }
}
