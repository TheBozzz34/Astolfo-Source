package bypass.auth.net;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class PacketChannel implements Channel {
   private static final ConnectionOptions v = ConnectionOptions.M(32);
   private final ConnectionListener I;
   private final Map<Short, PacketReader<?>> y;
   private final Map<Short, List<PacketListener<?>>> E;
   private final PacketOutput Q;
   private final ConnectionHandlerFactory w;
   private final Consumer<IOException> s;
   private final Runnable J;

   public PacketChannel(
      ConnectionListener connectionListener,
      Map<Short, PacketReader<?>> map,
      Map<Short, List<PacketListener<?>>> map2,
      PacketOutput packetOutput,
      ConnectionHandlerFactory connectionHandlerFactory,
      Consumer<IOException> consumer,
      Runnable runnable
   ) {
      this.I = connectionListener;
      this.y = map;
      this.E = map2;
      this.Q = packetOutput;
      this.w = connectionHandlerFactory;
      this.s = consumer;
      this.J = runnable;
   }

   public static PacketChannelBuilder U() {
      return new PacketChannelBuilder();
   }

   @Override
   public Connection K() {
      return new SocketConnection(this.I, this.y, this.E, this.Q, this.w, this.s, this.J);
   }
}
