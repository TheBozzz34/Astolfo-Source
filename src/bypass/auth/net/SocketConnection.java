package bypass.auth.net;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SocketConnection implements Connection {
   public final PacketConnection packetConnection;
   public final ConnectionHandlerFactory handlerFactory;
   private SocketChannel socketChannel;

   public SocketConnection(
      ConnectionListener connectionListener,
      Map<Short, PacketReader<?>> map,
      Map<Short, List<PacketListener<?>>> map2,
      PacketOutput packetOutput,
      ConnectionHandlerFactory connectionHandlerFactory,
      Consumer<IOException> consumer,
      Runnable runnable
   ) {
      this.packetConnection = new PacketConnection(connectionListener, map, map2, packetOutput, () -> {
         this.registerChannel(consumer);
         runnable.run();
      });
      this.handlerFactory = connectionHandlerFactory;
   }

   private void registerChannel(Consumer<IOException> consumer) {
      try {
         this.handlerFactory.createHandler(this.packetConnection).handleConnected(this.socketChannel);
      } catch (IOException ioException) {
         consumer.accept(ioException);
      }
   }

   @Override
   public void open(SocketChannel socketChannel) throws IOException {
      this.socketChannel = socketChannel;
      this.packetConnection.start(socketChannel);
   }

   @Override
   public void close() throws IOException {
      this.packetConnection.close();
      this.socketChannel.close();
   }
}
