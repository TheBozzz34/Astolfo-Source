package bypass.auth.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class PacketChannel implements Channel {
   private static final ConnectionOptions options = ConnectionOptions.ofSize(32);
   private final ConnectionListener connectionListener;
   private final Map<Short, PacketReader<?>> readers;
   private final Map<Short, List<PacketListener<?>>> listeners;
   private final PacketOutput packetOutput;
   private final ConnectionHandlerFactory handlerFactory;
   private final Consumer<IOException> errorHandler;
   private final Runnable onConnect;

   public PacketChannel(
      ConnectionListener connectionListener,
      Map<Short, PacketReader<?>> map,
      Map<Short, List<PacketListener<?>>> map2,
      PacketOutput packetOutput,
      ConnectionHandlerFactory connectionHandlerFactory,
      Consumer<IOException> consumer,
      Runnable runnable
   ) {
      this.connectionListener = connectionListener;
      this.readers = map;
      this.listeners = map2;
      this.packetOutput = packetOutput;
      this.handlerFactory = connectionHandlerFactory;
      this.errorHandler = consumer;
      this.onConnect = runnable;
   }

   public static PacketChannelBuilder newBuilder() {
      return new PacketChannelBuilder();
   }

   @Override
   public Connection getConnection() {
      return new SocketConnection(
         this.connectionListener, this.readers, this.listeners, this.packetOutput, this.handlerFactory, this.errorHandler, this.onConnect
      );
   }
}
