package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ThreadedSocketConnection extends SocketConnection {
   private final Consumer<IOException> o;
   private Thread V;

   public ThreadedSocketConnection(
      ConnectionListener connectionListener,
      Map<Short, PacketReader<?>> map,
      Map<Short, List<PacketListener<?>>> map2,
      PacketOutput packetOutput,
      ConnectionHandlerFactory connectionHandlerFactory,
      Consumer<IOException> consumer,
      Consumer<IOException> consumer2,
      Runnable runnable
   ) {
      super(connectionListener, map, map2, packetOutput, connectionHandlerFactory, consumer, runnable);
      this.o = consumer2;
   }

   @Override
   public void open(SocketChannel socketChannel) {
      this.V = new Thread(() -> {
         try {
            super.open(socketChannel);
         } catch (IOException ioException) {
            this.o.accept(ioException);
         }
      });
      this.V.start();
   }

   @Override
   public void close() throws IOException {
      if (this.V != null) {
         this.V.interrupt();
      }

      super.close();
   }
}
