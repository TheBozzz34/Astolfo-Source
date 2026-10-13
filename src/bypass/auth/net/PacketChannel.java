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
      ConnectionListener var1,
      Map<Short, PacketReader<?>> var2,
      Map<Short, List<PacketListener<?>>> var3,
      PacketOutput var4,
      ConnectionHandlerFactory var5,
      Consumer<IOException> var6,
      Runnable var7
   ) {
      this.I = var1;
      this.y = var2;
      this.E = var3;
      this.Q = var4;
      this.w = var5;
      this.s = var6;
      this.J = var7;
   }

   public static PacketChannelBuilder U() {
      return new PacketChannelBuilder();
   }

   @Override
   public Connection K() {
      return new SocketConnection(this.I, this.y, this.E, this.Q, this.w, this.s, this.J);
   }
}
